package nexters.payout.domain;

import nexters.payout.domain.stock.Exchange;
import nexters.payout.domain.stock.Sector;
import nexters.payout.domain.stock.Stock;

public class StockFixture {
    public static final String TESLA = "TSLA";

    public static Stock createStock(String ticker, Double price, Integer volume) {
        return new Stock(ticker, "tesla", Sector.FINANCIAL_SERVICES, Exchange.NYSE.name(), "industry", price, volume);
    }

    public static Stock createStock(String ticker, Sector sector) {
        return new Stock(ticker, "tesla", sector, Exchange.NYSE.name(), "industry", 0.0, 0);
    }
}
