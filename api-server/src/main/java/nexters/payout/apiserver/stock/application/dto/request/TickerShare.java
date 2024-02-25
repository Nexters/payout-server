package nexters.payout.apiserver.stock.application.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public record TickerShare(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty
        String ticker,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @Min(value = 1)
        Integer share
) {
}
