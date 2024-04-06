package nexters.payout.domain;

import nexters.payout.domain.portfolio.domain.Portfolio;
import nexters.payout.domain.portfolio.domain.PortfolioStock;
import nexters.payout.domain.portfolio.domain.PortfolioStocks;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class PortfolioFixture {

    public static UUID STOCK_ID = UUID.randomUUID();

    public static Portfolio createPortfolio(Instant expireAt, List<PortfolioStock> stocks) {
        return new Portfolio(expireAt, stocks);
    }
}
