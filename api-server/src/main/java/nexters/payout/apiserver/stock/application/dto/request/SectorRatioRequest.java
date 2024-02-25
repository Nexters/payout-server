package nexters.payout.apiserver.stock.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SectorRatioRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @Valid
        @Size(min = 1)
        List<TickerShare> tickerShares
) {
}

