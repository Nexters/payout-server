package nexters.payout.apiserver.portfolio.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record PortfolioResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id
) {
}
