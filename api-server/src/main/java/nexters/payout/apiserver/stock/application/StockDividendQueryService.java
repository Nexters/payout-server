package nexters.payout.apiserver.stock.application;

import nexters.payout.apiserver.stock.application.dto.response.StockDetailResponse;

public interface StockDividendQueryService {
    StockDetailResponse getStockByTicker(final String ticker);
}
