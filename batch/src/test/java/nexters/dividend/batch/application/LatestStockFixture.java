package nexters.dividend.batch.application;

import nexters.dividend.batch.application.FinancialClient.StockData;
import nexters.dividend.domain.stock.Exchange;
import nexters.dividend.domain.stock.Sector;

public class LatestStockFixture {
    public static StockData createLatestStock(String ticker, Double price, Integer volume) {
        return new StockData(ticker, ticker, Exchange.AMEX.name(), Sector.FINANCIAL_SERVICES, "industry", price, volume, volume);
    }
}
