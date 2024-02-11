package nexters.payout.apiserver.stock.application.dto.request;

import jakarta.validation.constraints.Size;

import java.util.List;

public record SectorRatioRequest(
        @Size(min = 1)
        List<TickerShare> tickerShares
) {
}

