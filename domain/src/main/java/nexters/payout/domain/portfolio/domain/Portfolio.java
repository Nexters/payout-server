package nexters.payout.domain.portfolio.domain;

import jakarta.persistence.*;
import nexters.payout.domain.BaseEntity;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Entity
public class Portfolio extends BaseEntity {

    @Embedded
    private PortfolioStocks portfolioStocks;

    private Instant expireAt;

    private Integer hits;

    public Portfolio() {
        super(null);
    }

    public Portfolio(final UUID id, final Instant expireAt, List<PortfolioStock> stocks) {
        super(id);
        this.portfolioStocks = new PortfolioStocks(stocks);
        this.expireAt = expireAt;
        this.hits = 0;
    }

    public Portfolio(final Instant expireAt, List<PortfolioStock> stocks) {
        super(null);
        this.portfolioStocks = new PortfolioStocks(stocks);
        this.expireAt = expireAt;
        this.hits = 0;
    }

    public List<PortfolioStock> portfolioStocks() {
        return Collections.unmodifiableList(portfolioStocks.stockShares());
    }

    public void incrementHits() {
        hits++;
    }

    public boolean isExpired() {
        return expireAt.isAfter(Instant.now());
    }
}
