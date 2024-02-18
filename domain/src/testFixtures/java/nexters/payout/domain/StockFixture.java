package nexters.payout.domain;

import nexters.payout.domain.stock.domain.Exchange;
import nexters.payout.domain.stock.domain.Sector;
import nexters.payout.domain.stock.domain.Stock;

import java.util.UUID;

public class StockFixture {
    public static final String TSLA = "TSLA";
    public static final String AAPL = "AAPL";
    public static final String SBUX = "SBUX";

    public static Stock createStock(String ticker, Double price, Integer volume) {
        return new Stock(ticker, "tesla", Sector.FINANCIAL_SERVICES, Exchange.NYSE.name(), "industry", price, volume);
    }

    public static Stock createStock(String ticker, Sector sector) {
        return new Stock(UUID.randomUUID(), ticker, ticker, sector, Exchange.NYSE.name(), "industry", 0.0, 0);
    }

    public static Stock createStock(String ticker, String companyName) {
        return new Stock(UUID.randomUUID(), ticker, companyName, Sector.TECHNOLOGY, Exchange.NYSE.name(), "industry", 0.0, 0);
    }

    public static Stock createStock(String ticker, Sector sector, Double price) {
        return new Stock(UUID.randomUUID(), ticker, ticker, sector, Exchange.NYSE.name(), "industry", price, 0);
    }
}
