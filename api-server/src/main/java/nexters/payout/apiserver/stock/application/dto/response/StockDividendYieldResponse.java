package nexters.payout.apiserver.stock.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

public record StockDividendYieldResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<SingleStockDividendYieldResponse> dividends,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant lastModifiedAt
) {
    public static StockDividendYieldResponse of(List<SingleStockDividendYieldResponse> dividends) {
        return dividends.isEmpty() ? new StockDividendYieldResponse(dividends, null) :
                new StockDividendYieldResponse(dividends, dividends.get(0).lastModifiedAt());
    }
}
