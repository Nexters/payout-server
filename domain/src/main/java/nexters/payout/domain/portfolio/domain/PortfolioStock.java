package nexters.payout.domain.portfolio.domain;

import nexters.payout.domain.BaseEntity;

import java.util.UUID;

public class PortfolioStock extends BaseEntity {

    private UUID portfolioId;
    private UUID stockId;
    private Integer shares;


    public PortfolioStock(final UUID id, final UUID portfolioId, final UUID stockId, final Integer shares) {
        super(id);
        this.portfolioId = portfolioId;
        this.stockId = stockId;
        this.shares = shares;
    }

    private PortfolioStock(final UUID portfolioId, final UUID stockId, final Integer shares) {
        super(null);
        this.portfolioId = portfolioId;
        this.stockId = stockId;
        this.shares = shares;
    }

    public PortfolioStock create(final UUID portfolioId, final UUID stockId, final Integer shares) {
        return new PortfolioStock(portfolioId, stockId, shares);
    }
}
