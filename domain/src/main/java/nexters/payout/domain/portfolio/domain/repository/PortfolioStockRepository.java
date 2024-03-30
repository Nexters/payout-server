package nexters.payout.domain.portfolio.domain.repository;

import nexters.payout.domain.portfolio.domain.PortfolioStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PortfolioStockRepository extends JpaRepository<PortfolioStock, UUID> {
    List<PortfolioStock> findAllByPortfolioId(UUID portfolioId);
}
