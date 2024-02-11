package nexters.payout.apiserver.stock.application.dto.request;

import jakarta.validation.constraints.Min;

public record TickerShare(
        String ticker,

        @Min(value = 1)
        Integer share
) {

}
