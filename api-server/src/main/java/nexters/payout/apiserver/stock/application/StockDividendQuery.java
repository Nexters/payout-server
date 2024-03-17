package nexters.payout.apiserver.stock.application;

import nexters.payout.apiserver.stock.application.dto.response.StockDetailResponse;

public interface StockDividendQuery {
    StockDetailResponse getStockByTicker(final String ticker);
}
