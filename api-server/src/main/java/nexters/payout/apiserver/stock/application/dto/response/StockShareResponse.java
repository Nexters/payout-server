package nexters.payout.apiserver.stock.application.dto.response;

import nexters.payout.domain.stock.domain.service.SectorAnalysisService.StockShare;

public record StockShareResponse(
        StockResponse stockResponse,
        Integer share
) {

    public static StockShareResponse from(StockShare stockShare) {
        return new StockShareResponse(
                StockResponse.from(stockShare.stock()),
                stockShare.share()
        );
    }
}
