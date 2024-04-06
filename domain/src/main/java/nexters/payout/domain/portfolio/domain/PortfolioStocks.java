package nexters.payout.domain.portfolio.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@Embeddable
public class PortfolioStocks {

    @ElementCollection
    @CollectionTable(name = "portfolio_stock", joinColumns = @JoinColumn(name = "portfolio_id"))
    private List<PortfolioStock> portfolioStocks = new ArrayList<>();

    public PortfolioStocks(List<PortfolioStock> stocks) {
        if (stocks.isEmpty()) {
            throw new IllegalArgumentException("portfolioStocks must not be empty");
        }
        portfolioStocks = stocks;
    }

    public List<PortfolioStock> getPortfolioStocks() {
        return Collections.unmodifiableList(portfolioStocks);
    }
}
