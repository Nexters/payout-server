package nexters.payout.domain.portfolio.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import nexters.payout.domain.BaseEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Getter
public class Portfolio extends BaseEntity {

    @ElementCollection
    @CollectionTable(name = "portfolio_stock", joinColumns = @JoinColumn(name = "portfolio_id"))
    private List<PortfolioStock> stocks = new ArrayList<>();
    private Instant expireAt;

    public Portfolio() {
        super(null);
    }

    public Portfolio(final UUID id, final Instant expireAt) {
        super(id);
        this.expireAt = expireAt;
    }

    private Portfolio(final Instant expireAt) {
        super(null);
        this.expireAt = expireAt;
    }

    public static Portfolio create(final Instant expireAt) {
        return new Portfolio(expireAt);
    }

    public boolean isExpired() {
        return expireAt.isAfter(Instant.now());
    }
}
