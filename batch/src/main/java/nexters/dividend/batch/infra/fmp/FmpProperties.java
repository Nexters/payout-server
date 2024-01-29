package nexters.dividend.batch.infra.fmp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("financial.fmp")
@RequiredArgsConstructor
@Getter
public class FmpProperties {
    final String apiKey;
    final String baseUrl;
    final String stockListPath;
    final String stockScreenerPath;
    final String exchangeSymbolsStockListPath;
    final String stockDividendCalenderPath;
}
