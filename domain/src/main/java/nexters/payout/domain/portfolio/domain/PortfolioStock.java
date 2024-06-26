package nexters.payout.domain.portfolio.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortfolioStock {

    private UUID stockId;
    private Integer shares;

    public PortfolioStock(final UUID stockId, final Integer shares) {
        this.stockId = stockId;
        this.shares = shares;
    }
}
