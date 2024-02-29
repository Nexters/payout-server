package nexters.payout.apiserver.stock.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import nexters.payout.domain.stock.domain.Stock;

import java.util.UUID;

public record StockResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID stockId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String ticker,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String companyName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String sectorName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String sectorValue,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String exchange,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String industry,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Double price,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Integer volume,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String logoUrl
) {
    public static StockResponse from(final Stock stock) {
        return new StockResponse(
                stock.getId(),
                stock.getTicker(),
                stock.getName(),
                stock.getSector().getName(),
                stock.getSector().name(),
                stock.getExchange(),
                stock.getIndustry(),
                stock.getPrice(),
                stock.getVolume(),
                stock.getLogoUrl()
        );
    }
}
