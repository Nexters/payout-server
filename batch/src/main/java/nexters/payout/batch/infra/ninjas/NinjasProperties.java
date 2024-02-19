package nexters.payout.batch.infra.ninjas;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("financial.ninjas")
@RequiredArgsConstructor
@Getter
public class NinjasProperties {
    final String apiKey;
    final String baseUrl;
    final String logoPath;
}
