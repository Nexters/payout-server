package nexters.dividend.batch.application;

import nexters.dividend.batch.application.FinancialClient.LatestStock;
import nexters.dividend.domain.stock.Exchange;
import nexters.dividend.domain.stock.Sector;

public class LatestStockFixture {
    public static LatestStock createLatestStock(String ticker, Double price, Integer volume) {
        return new LatestStock(ticker, ticker, Exchange.AMEX.name(), Sector.FINANCIAL_SERVICES, "industry", price, volume, volume);
    }
}
