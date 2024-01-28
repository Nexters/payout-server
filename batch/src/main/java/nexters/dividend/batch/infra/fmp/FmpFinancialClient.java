package nexters.dividend.batch.infra.fmp;

import lombok.extern.slf4j.Slf4j;
import nexters.dividend.batch.application.FinancialClient;
import nexters.dividend.domain.stock.Exchange;
import nexters.dividend.domain.stock.Sector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    public List<LatestStock> getLatestStockList() {
        Map<String, StockData> stockDataMap = getFmpDataList().stream().distinct()
                .collect(Collectors.toMap(StockData::symbol, stockData -> stockData, (first, second) -> first));

        Map<String, VolumeData> volumeDataMap = Arrays.stream(Exchange.values())
                .flatMap(exchange -> getVolumeList(exchange).stream().distinct())
                .collect(Collectors.toMap(VolumeData::symbol, volumeData -> volumeData));


        return stockDataMap.entrySet().stream()
                .map(entry -> {
                    String tickerName = entry.getKey();
                    StockData stockData = entry.getValue();
                    VolumeData volumeData = volumeDataMap.getOrDefault(tickerName, new VolumeData(tickerName, null, null));

                    return new LatestStock(
                            tickerName,
                            stockData.exchangeShortName(),
                            stockData.price(),
                            stockData.companyName(),
                            Sector.fromValue(stockData.sector()),
                            stockData.industry(),
                            volumeData.volume(),
                            volumeData.avgVolume()
                    );
                })
                .toList();
    }

    private List<StockData> getFmpDataList() {
        return fmpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(fmpProperties.getStockScreenerPath())
                        .queryParam("apikey", fmpProperties.getApiKey())
                        .queryParam("exchange", Exchange.getNames())
                        .queryParam("limit", MAX_LIMIT)
                        .build())
                .retrieve()
                .bodyToFlux(StockData.class)
                .collectList()
                .block();
    }

    private List<VolumeData> getVolumeList(Exchange exchange) {
        return fmpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(fmpProperties.getExchangeSymbolsStockListPath() + exchange.name())
                        .queryParam("apikey", fmpProperties.getApiKey())
                        .build())
                .retrieve()
                .bodyToFlux(VolumeData.class)
                .collectList()
                .block();
    }
}
