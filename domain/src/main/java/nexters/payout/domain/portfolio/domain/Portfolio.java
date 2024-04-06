package nexters.payout.domain.portfolio.domain;

import jakarta.persistence.*;
import lombok.Getter;
import nexters.payout.domain.BaseEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Getter
public class Portfolio extends BaseEntity {

    @Embedded
    private PortfolioStocks portfolioStocks;

    private Instant expireAt;

    public Portfolio() {
        super(null);
    }

    public Portfolio(final Instant expireAt, List<PortfolioStock> stocks) {
        super(null);
        this.portfolioStocks = new PortfolioStocks(stocks);
        this.expireAt = expireAt;
    }

    public boolean isExpired() {
        return expireAt.isAfter(Instant.now());
    }
}
