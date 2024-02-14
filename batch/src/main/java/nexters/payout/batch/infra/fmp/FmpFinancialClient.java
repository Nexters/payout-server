package nexters.payout.batch.infra.fmp;

import lombok.extern.slf4j.Slf4j;
import nexters.payout.batch.application.FinancialClient;
import nexters.payout.domain.stock.domain.Exchange;
import nexters.payout.domain.stock.domain.Sector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        Map<String, FmpStockData> stockDataMap = Sector.getNames()
                .stream()
                .flatMap(it -> fetchStockList(it).stream())
                .collect(Collectors.toMap(FmpStockData::symbol, fmpStockData -> fmpStockData, (first, second) -> first));

        Map<String, FmpVolumeData> volumeDataMap = Arrays
                .stream(Exchange.values())
                .flatMap(exchange -> fetchVolumeList(exchange).stream())
                .collect(Collectors.toMap(FmpVolumeData::symbol, fmpVolumeData -> fmpVolumeData));

        return stockDataMap.entrySet().stream()
                .map(entry -> {
                    String tickerName = entry.getKey();
                    FmpStockData fmpStockData = entry.getValue();
                    FmpVolumeData fmpVolumeData = volumeDataMap
                            .getOrDefault(tickerName, new FmpVolumeData(tickerName, null, null));

                    return new StockData(
                            tickerName,
                            fmpStockData.companyName(),
                            fmpStockData.exchangeShortName(),
                            Sector.fromValue(fmpStockData.sector()),
                            fmpStockData.industry(),
                            fmpStockData.price(),
                            fmpVolumeData.volume(),
                            fmpVolumeData.avgVolume()
                    );
                })
                .toList();
    }

    private List<FmpStockData> fetchStockList(String sector) {
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
                .block();
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

    /**
     * 배당금 관련 정보를 업데이트하는 메서드입니다.
     */
    @Override
    public List<DividendData> getDividendList() {

        // TODO (작년 1월 ~ 12월 데이터 고정적으로 가져오도록 수정 필요)
        // 3개월 간 총 4번의 데이터를 조회함으로써 기준 날짜로부터 이전 1년 간의 데이터를 조회
        List<DividendData> result = new ArrayList<>();
        for (int i = 0; i < 4; i++) {

            Instant date = ZonedDateTime.now(ZoneOffset.UTC).minusDays(1).minusMonths(i).toInstant();

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

    private List<FmpDividendData> fetchDividendList(Instant date) {
        return fmpWebClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(fmpProperties.getStockDividendCalenderPath())
                                .queryParam("to", formatInstant(date))
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

    /**
     * Instant를 yyyy-MM-dd 형식의 String으로 변환하는 메서드입니다.
     *
     * @param instant instant 데이터
     * @return 날짜 String 데이터
     */
    private String formatInstant(Instant instant) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(Date.from(instant));
    }
}
