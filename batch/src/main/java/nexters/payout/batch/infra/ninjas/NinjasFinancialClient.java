package nexters.payout.batch.infra.ninjas;

import lombok.extern.slf4j.Slf4j;
import nexters.payout.batch.application.StockLogo;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class NinjasFinancialClient implements StockLogo {

    private final WebClient ninjasWebClient;
    private final NinjasProperties ninjasProperties;

    NinjasFinancialClient(final NinjasProperties ninjasProperties) {
        this.ninjasProperties = ninjasProperties;
        this.ninjasWebClient = WebClient.builder()
                .baseUrl(ninjasProperties.getBaseUrl())
                .defaultHeader("X-Api-Key", ninjasProperties.getApiKey())
                .build();
    }

    @Override
    public String getLogoUrl(String ticker) {
        return fetchLogoUrl(ticker).image();
    }

    private NinjasStockLogo fetchLogoUrl(String ticker) {
        return ninjasWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(ninjasProperties.getLogoPath())
                        .queryParam("ticker", ticker)
                        .build())
                .retrieve()
                .bodyToFlux(NinjasStockLogo.class)
                .next()
                .doOnError(e -> log.error("fetchLogoUrl 호출 실패: {}", e.getMessage()))
                .onErrorReturn(new NinjasStockLogo(ticker, ticker, null))
                .blockOptional()
                .orElse(new NinjasStockLogo(ticker, ticker, null));
    }
}
