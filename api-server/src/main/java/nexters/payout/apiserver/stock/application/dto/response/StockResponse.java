package nexters.payout.apiserver.stock.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import nexters.payout.domain.stock.domain.Stock;

import java.util.UUID;

public record StockResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "stock id")
        UUID stockId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ticker")
        String ticker,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "company name")
        String companyName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "sector name")
        String sectorName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "exchange")
        String exchange,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "industry")
        String industry,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "price")
        Double price,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "volume")
        Integer volume,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "logo url")
        String logoUrl
) {
    public static StockResponse from(Stock stock) {
        return new StockResponse(
                stock.getId(),
                stock.getTicker(),
                stock.getName(),
                stock.getSector().getName(),
                stock.getExchange(),
                stock.getIndustry(),
                stock.getPrice(),
                stock.getVolume(),
                stock.getLogoUrl()
        );
    }
}
