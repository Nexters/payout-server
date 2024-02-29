package nexters.payout.batch.infra.fmp;

import lombok.extern.slf4j.Slf4j;
import nexters.payout.batch.application.FinancialClient;
import nexters.payout.core.time.DateFormat;
import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.stock.domain.Exchange;
import nexters.payout.domain.stock.domain.Sector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.ZoneOffset.UTC;
import static nexters.payout.domain.stock.domain.Sector.ETC;
import static nexters.payout.domain.stock.domain.Sector.ETF;

@Slf4j
@Service
public class FmpFinancialClient implements FinancialClient {

    private final WebClient fmpWebClient;
    private final FmpProperties fmpProperties;
    private final static int MAX_LIMIT = 1000000;

    FmpFinancialClient(final FmpProperties fmpProperties) {
        this.fmpProperties = fmpProperties;
        this.fmpWebClient = WebClient.builder()
                .baseUrl(fmpProperties.getBaseUrl())
                .build();
    }

    @Override
    public List<StockData> getLatestStockList() {
        Map<String, FmpStockData> stockDataMap = Stream.concat(
                        Sector.getNames()
                                .stream()
                                .filter(sector -> !(sector.equals(ETC.getName()) || sector.equals(ETF.getName())))
                                .flatMap(this::fetchStockList)
                        , fetchEtfStockList())
                .collect(Collectors.toMap(FmpStockData::getSymbol, Function.identity(), (first, second) -> first));

        Map<String, FmpVolumeData> volumeDataMap = Arrays
                .stream(Exchange.values())
                .flatMap(exchange -> fetchVolumeList(exchange).stream())
                .collect(Collectors.toMap(FmpVolumeData::symbol, fmpVolumeData -> fmpVolumeData));

        return stockDataMap.entrySet()
                .stream()
                .map(entry -> {
                    String tickerName = entry.getKey();
                    FmpStockData fmpStockData = entry.getValue();
                    FmpVolumeData fmpVolumeData = volumeDataMap
                            .getOrDefault(tickerName, new FmpVolumeData(tickerName, null, null));

                    return new StockData(
                            tickerName,
                            fmpStockData.getCompanyName(),
                            fmpStockData.getExchangeShortName(),
                            Sector.fromName(fmpStockData.getSector()),
                            fmpStockData.getIndustry(),
                            fmpStockData.getPrice(),
                            fmpVolumeData.volume(),
                            fmpVolumeData.avgVolume()
                    );
                })
                .toList();
    }

    private Stream<FmpStockData> fetchStockList(final String sector) {
        return fmpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(fmpProperties.getStockScreenerPath())
                        .queryParam("apikey", fmpProperties.getApiKey())
                        .queryParam("exchange", Exchange.getNames())
                        .queryParam("sector", sector)
                        .queryParam("limit", MAX_LIMIT)
                        .build())
                .retrieve()
                .bodyToFlux(FmpStockData.class)
                .collectList()
                .block()
                .stream();
    }

    private Stream<FmpStockData> fetchEtfStockList() {
        return fmpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(fmpProperties.getStockScreenerPath())
                        .queryParam("apikey", fmpProperties.getApiKey())
                        .queryParam("isEtf", true)
                        .build())
                .retrieve()
                .bodyToFlux(FmpStockData.class)
                .map(fmpStockData -> {
                    fmpStockData.setSector(ETF.getName());
                    return fmpStockData;
                })
                .collectList()
                .block()
                .stream();
    }

    private List<FmpVolumeData> fetchVolumeList(final Exchange exchange) {
        return fmpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(fmpProperties.getExchangeSymbolsStockListPath() + exchange.name())
                        .queryParam("apikey", fmpProperties.getApiKey())
                        .build())
                .retrieve()
                .bodyToFlux(FmpVolumeData.class)
                .collectList()
                .block();
    }

    @Override
    public List<DividendData> getPastDividendList() {

        // 현재 시간을 기준으로 작년 1월 ~ 12월의 배당금 데이터를 조회
        List<DividendData> result = new ArrayList<>();
        for (int month = 12; month >= 3; month -= 3) {

            Instant date = LocalDate.of(
                            InstantProvider.getLastYear(),
                            month,
                            1)
                    .atStartOfDay()
                    .toInstant(UTC);

            List<DividendData> dividendResponses = fetchDividendList(date)
                    .stream()
                    .map(FmpDividendData::toDividendData)
                    .toList();

            if (dividendResponses.isEmpty()) {
                log.error("FmpClient updateDividendData 수행 중 에러 발생: dividendResponses is empty");
                continue;
            }

            result.addAll(dividendResponses);
        }

        return result;
    }

    @Override
    public List<DividendData> getUpcomingDividendList() {

        List<DividendData> dividendResponse = fetchDividendList(
                LocalDate.now().atStartOfDay().toInstant(UTC),
                LocalDate.now().plusMonths(3).atStartOfDay().toInstant(UTC)
        )
                .stream()
                .map(FmpDividendData::toDividendData)
                .toList();

        if (dividendResponse.isEmpty()) {
            log.error("FmpClient updateDividendData 수행 중 에러 발생: dividendResponses is empty");
        }

        return dividendResponse;
    }

    private List<FmpDividendData> fetchDividendList(Instant date) {
        return fmpWebClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(fmpProperties.getStockDividendCalenderPath())
                                .queryParam("to", DateFormat.formatInstant(date))
                                .queryParam("apikey", fmpProperties.getApiKey())
                                .build())
                .retrieve()
                .bodyToFlux(FmpDividendData.class)
                .onErrorResume(throwable -> {
                    log.error("FmpClient updateDividendData 수행 중 에러 발생: {}", throwable.getMessage());
                    return Mono.empty();
                })
                .collectList()
                .block();
    }

    private List<FmpDividendData> fetchDividendList(Instant from, Instant to) {
        return fmpWebClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(fmpProperties.getStockDividendCalenderPath())
                                .queryParam("from", DateFormat.formatInstant(from))
                                .queryParam("to", DateFormat.formatInstant(to))
                                .queryParam("apikey", fmpProperties.getApiKey())
                                .build())
                .retrieve()
                .bodyToFlux(FmpDividendData.class)
                .onErrorResume(throwable -> {
                    log.error("FmpClient updateDividendData 수행 중 에러 발생: {}", throwable.getMessage());
                    return Mono.empty();
                })
                .collectList()
                .block();
    }
}
