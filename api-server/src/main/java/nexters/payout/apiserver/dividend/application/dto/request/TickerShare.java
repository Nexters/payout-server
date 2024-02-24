package nexters.payout.apiserver.dividend.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public record TickerShare(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ticker name")
        @NotEmpty
        String ticker,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "share")
        @Min(value = 1)
        Integer share
) { }