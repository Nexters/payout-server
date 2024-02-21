package nexters.payout.apiserver.dividend.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public record TickerShare(
        @NotEmpty
        String ticker,
        @Min(value = 1)
        Integer share
) { }