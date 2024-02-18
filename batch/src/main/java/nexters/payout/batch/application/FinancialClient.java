package nexters.payout.batch.application;

import nexters.payout.domain.stock.domain.Sector;
import nexters.payout.domain.stock.domain.Stock;

import java.time.Instant;
import java.util.List;

public interface FinancialClient {

    List<StockData> getLatestStockList();

    List<DividendData> getDividendList();

    record StockData(
            String ticker,
            String name,
            String exchange,
            Sector sector,
            String industry,
            Double price,
            Integer volume,
            Integer avgVolume
    ) {
        Stock toDomain() {
            return new Stock(ticker, name, sector, exchange, industry, price, volume);
        }
    }

    record DividendData(
            Instant exDividendDate,
            String label,
            Double adjDividend,
            String symbol,
            Double dividend,
            Instant recordDate,
            Instant paymentDate,
            Instant declarationDate
    ) {

    }
}