package nexters.payout.domain;

import nexters.payout.domain.portfolio.domain.Portfolio;
import nexters.payout.domain.portfolio.domain.PortfolioStock;

import java.time.Instant;
import java.util.UUID;

public class PortfolioFixture {

    public static Portfolio createPortfolio(Instant expireAt) {
        return new Portfolio(UUID.randomUUID(), expireAt);
    }

    public static PortfolioStock createPortfolioStock(UUID portfolioId, UUID stockId, Integer shares) {
        return new PortfolioStock(UUID.randomUUID(), stockId, shares);
    }
}
