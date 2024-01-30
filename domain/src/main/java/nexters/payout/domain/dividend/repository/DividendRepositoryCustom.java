package nexters.payout.domain.dividend.repository;


import nexters.payout.domain.dividend.Dividend;

import java.time.Instant;
import java.util.Optional;

/**
 * custom query를 위한 dividend repository interface 입니다.
 *
 * @author Min Ho CHO
 */
public interface DividendRepositoryCustom {

    Optional<Dividend> findByTickerAndExDividendDate(String ticker, Instant exDividendDate);
}
