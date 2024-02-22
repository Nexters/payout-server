package nexters.payout.batch.application;

import nexters.payout.domain.stock.domain.Sector;
import nexters.payout.domain.stock.domain.Stock;

import java.time.Instant;
import java.util.List;

public interface FinancialClient {

    List<StockData> getLatestStockList();

    List<DividendData> getPastDividendList();

    List<DividendData> getUpcomingDividendList();

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
            return new Stock(ticker, name, sector, exchange, industry, price, volume, null);
        }

        Stock toDomain(String logoUrl) {
            return new Stock(ticker, name, sector, exchange, industry, price, volume, logoUrl);
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