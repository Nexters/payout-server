package nexters.payout.domain.portfolio.domain.repository;

import nexters.payout.domain.portfolio.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {

}
