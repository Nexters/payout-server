package nexters.payout.apiserver.dividend.infra.eodhd;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("financial.eodhd")
@RequiredArgsConstructor
@Getter
public class EodhdProperties {
    final String baseUrl;
    final String stockLogoPath;
    final String imagePostfix;
}
