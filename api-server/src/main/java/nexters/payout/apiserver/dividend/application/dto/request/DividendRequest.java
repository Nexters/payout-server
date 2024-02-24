package nexters.payout.apiserver.dividend.application.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

public record DividendRequest(
        @Parameter(required = true, example = "ticker and share")
        @Valid
        @Size(min = 1)
        List<TickerShare> tickerShares
) {
}
