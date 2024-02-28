package nexters.payout.apiserver.stock.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import nexters.payout.domain.stock.domain.Stock;

import java.time.Instant;
import java.util.UUID;

public record SingleStockDividendYieldResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID stockId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String ticker,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String logoUrl,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Double dividendYield
) {

    public static SingleStockDividendYieldResponse of(final Stock stock, final Double dividendYield) {
        return new SingleStockDividendYieldResponse(
                stock.getId(),
                stock.getTicker(),
                stock.getLogoUrl(),
                dividendYield
        );
    }
}
