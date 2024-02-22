package nexters.payout.domain.dividend.infra;


import nexters.payout.domain.dividend.domain.Dividend;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * custom query를 위한 dividend repository interface 입니다.
 *
 * @author Min Ho CHO
 */
public interface DividendRepositoryCustom {

    Optional<Dividend> findByTickerAndExDividendDate(String ticker, Instant exDividendDate);
    List<Dividend> findAllByTickerAndYearAndMonth(String ticker, Integer year, Integer month);
    List<Dividend> findAllByTickerAndYear(String ticker, Integer year);
    void deleteByYearAndCreatedAt(Integer year, Instant createdAt);
}
