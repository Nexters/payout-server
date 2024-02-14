package nexters.payout.domain.dividend.domain.repository;

import nexters.payout.domain.dividend.domain.Dividend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 배당금 JPA repository 클래스입니다.
 *
 * @author Min Ho CHO
 */
public interface DividendRepository extends JpaRepository<Dividend, UUID>, DividendRepositoryCustom {

    List<Dividend> findAllByStockId(UUID stockId);

    List<Dividend> findAllByStockIdIn(List<UUID> stockIds);

    Optional<Dividend> findByStockIdAndExDividendDate(UUID stockId, Instant exDividendDate);
}
