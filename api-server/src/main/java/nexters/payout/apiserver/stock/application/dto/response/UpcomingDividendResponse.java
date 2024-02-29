package nexters.payout.apiserver.stock.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

public record UpcomingDividendResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<SingleUpcomingDividendResponse> dividends,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant lastModifiedAt
) {
    public static UpcomingDividendResponse of(List<SingleUpcomingDividendResponse> dividends) {
        return dividends.isEmpty() ? new UpcomingDividendResponse(dividends, null) :
                new UpcomingDividendResponse(dividends, dividends.get(0).lastModifiedAt());
    }
}
