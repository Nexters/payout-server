package nexters.dividend.batch.application;

import nexters.dividend.domain.stock.Sector;
import nexters.dividend.domain.stock.Stock;

import java.util.List;

public interface FinancialClient {

    List<LatestStock> getLatestStockList();

    record LatestStock(
            String ticker,
            String exchange,
            Double price,
            String name,
            Sector sector,
            String industry,
            Integer volume,
            Integer avgVolume
    ) {
        Stock toDomain() {
            return new Stock(ticker, name, sector, exchange, industry, price, volume);
        }
    }
}