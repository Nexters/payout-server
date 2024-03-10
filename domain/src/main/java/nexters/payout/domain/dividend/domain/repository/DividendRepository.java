package nexters.payout.domain.dividend.domain.repository;

import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.dividend.infra.DividendRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DividendRepository extends JpaRepository<Dividend, UUID>, DividendRepositoryCustom {
    List<Dividend> findAllByStockId(UUID stockId);
}
