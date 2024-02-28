package nexters.payout.apiserver.stock.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.stock.domain.Stock;

import java.time.Instant;
import java.util.UUID;

public record SingleUpcomingDividendResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID stockId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String ticker,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String logoUrl,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant exDividendDate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant lastModifiedAt
) {
    public static SingleUpcomingDividendResponse of(final Stock stock, final Dividend dividend) {
        return new SingleUpcomingDividendResponse(
                stock.getId(),
                stock.getTicker(),
                stock.getLogoUrl(),
                dividend.getExDividendDate(),
                dividend.getLastModifiedAt()
        );
    }
}
