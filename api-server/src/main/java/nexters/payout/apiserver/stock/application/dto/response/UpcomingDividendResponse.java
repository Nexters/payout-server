package nexters.payout.apiserver.stock.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.stock.domain.Stock;

import java.time.Instant;
import java.util.UUID;

public record UpcomingDividendResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "stock id")
        UUID stockId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ticker")
        String ticker,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "logo url")
        String logoUrl,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ex dividend date")
        Instant exDividendDate
) {
    public static UpcomingDividendResponse of(Stock stock, Dividend dividend) {
        return new UpcomingDividendResponse(
                stock.getId(),
                stock.getTicker(),
                stock.getLogoUrl(),
                dividend.getExDividendDate()
        );
    }
}
