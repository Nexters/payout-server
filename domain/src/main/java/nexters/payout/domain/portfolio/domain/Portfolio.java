package nexters.payout.domain.portfolio.domain;

import jakarta.persistence.Entity;
import lombok.Getter;
import nexters.payout.domain.BaseEntity;

import java.time.Instant;
import java.util.UUID;


@Entity
@Getter
public class Portfolio extends BaseEntity {

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
