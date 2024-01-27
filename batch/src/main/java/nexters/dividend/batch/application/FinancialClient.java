package nexters.dividend.batch.application;

import java.util.List;

public interface FinancialClient {

    List<StockData> getStockList();

    record StockData(
            String symbol,
            String exchange,
            Double price,
            String name) {
    }
}