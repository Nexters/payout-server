package nexters.payout.domain.portfolio.domain.repository;

import nexters.payout.domain.portfolio.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {
    List<Portfolio> findByExpireAtBefore(Instant date);

    @Modifying(clearAutomatically = true)
    @Query("delete from Portfolio p where p.id in :ids")
    void deleteAllByIdInQuery(List<UUID> ids);
}
