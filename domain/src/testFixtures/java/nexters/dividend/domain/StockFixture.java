package nexters.dividend.domain;

import nexters.dividend.domain.stock.Exchange;
import nexters.dividend.domain.stock.Sector;
import nexters.dividend.domain.stock.Stock;

public class StockFixture {
    public static final String TESLA = "TSLA";

    public static Stock createStock(String ticker, Double price, Integer volume) {
        return new Stock(ticker, "tesla", Sector.FINANCIAL_SERVICES, Exchange.NYSE.name(), "industry", price, volume);
    }
}
