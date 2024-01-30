package nexters.payout.batch.application;

import nexters.payout.domain.stock.Exchange;
import nexters.payout.domain.stock.Sector;

public class LatestStockFixture {
    public static FinancialClient.StockData createLatestStock(String ticker, Double price, Integer volume) {
        return new FinancialClient.StockData(ticker, ticker, Exchange.AMEX.name(), Sector.FINANCIAL_SERVICES, "industry", price, volume, volume);
    }
}
