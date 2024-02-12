package nexters.payout.domain.dividend.repository;

import nexters.payout.domain.dividend.Dividend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.time.LocalDate;
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

    @Query("""
                    SELECT d
                    FROM Dividend d
                    WHERE d.stockId = :stockId AND YEAR(d.paymentDate) = :year
                    ORDER BY d.paymentDate DESC
            """)
    List<Dividend> findAllByStockIdAndPaymentDateYear(UUID stockId, int year, Pageable pageable);

    List<Dividend> findAllByStockIdOrderByPaymentDateDesc(UUID stockId, Pageable pageable);

    List<Dividend> findAllByStockIdIn(List<UUID> stockIds);

    Optional<Dividend> findByStockIdAndExDividendDate(UUID stockId, Instant exDividendDate);
}
