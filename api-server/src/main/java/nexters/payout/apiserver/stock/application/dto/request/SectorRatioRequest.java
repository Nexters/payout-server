package nexters.payout.apiserver.stock.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SectorRatioRequest(
        @Valid
        @Size(min = 1)
        List<TickerShare> tickerShares
) {
}

