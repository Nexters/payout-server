package nexters.payout.apiserver.dividend.application.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public record TickerShare(
        @Parameter(required = true, example = "ticker name")
        @NotEmpty
        String ticker,
        @Parameter(required = true, example = "share")
        @Min(value = 1)
        Integer share
) { }