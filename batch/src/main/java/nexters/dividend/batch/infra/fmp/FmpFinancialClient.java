package nexters.dividend.batch.infra.fmp;

import nexters.dividend.batch.application.FinancialClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class FmpFinancialClient implements FinancialClient {
    private final WebClient webClient;
    private final FmpProperties fmpProperties;

    FmpFinancialClient(final FmpProperties fmpProperties) {
        this.fmpProperties = fmpProperties;
        this.webClient = WebClient.builder()
                .baseUrl(fmpProperties.getBaseUrl())
                .build();
    }

    @Override
    public List<StockData> getStockList() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(fmpProperties.getStockListPath())
                        .queryParam("apikey", fmpProperties.apiKey)
                        .build())
                .retrieve()
                .bodyToFlux(StockData.class)
                .collectList()
                .block();
    }
}
