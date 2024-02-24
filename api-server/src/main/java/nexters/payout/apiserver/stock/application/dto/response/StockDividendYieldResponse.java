package nexters.payout.apiserver.stock.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import nexters.payout.domain.stock.domain.Stock;

import java.util.UUID;

public record StockDividendYieldResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "stock id")
        UUID stockId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ticker")
        String ticker,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "logo url")
        String logoUrl,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "dividend yield")
        Double dividendYield
) {

    public static StockDividendYieldResponse of(Stock stock, Double dividendYield) {
        return new StockDividendYieldResponse(
                stock.getId(),
                stock.getTicker(),
                stock.getLogoUrl(),
                dividendYield
        );
    }
}
