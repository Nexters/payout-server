package nexters.payout.apiserver.stock.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import nexters.payout.domain.stock.domain.service.SectorAnalysisService.StockShare;

public record StockShareResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        StockResponse stockResponse,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Integer share
) {

    public static StockShareResponse from(final StockShare stockShare) {
        return new StockShareResponse(
                StockResponse.from(stockShare.stock()),
                stockShare.share()
        );
    }
}
