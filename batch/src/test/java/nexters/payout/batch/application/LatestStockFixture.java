package nexters.payout.batch.application;

import nexters.payout.domain.stock.Exchange;
import nexters.payout.domain.stock.Sector;
import nexters.payout.batch.application.FinancialClient.StockData;

public class LatestStockFixture {
    public static StockData createStockData(String ticker, Double price, Integer volume) {
        return new StockData(ticker, ticker, Exchange.AMEX.name(), Sector.FINANCIAL_SERVICES, "industry", price, volume, volume);
    }
}
