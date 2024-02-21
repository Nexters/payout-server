package nexters.payout.apiserver.dividend.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

public record DividendRequest(
        @Valid
        @Size(min = 1)
        List<TickerShare> tickerShares
) {
}
