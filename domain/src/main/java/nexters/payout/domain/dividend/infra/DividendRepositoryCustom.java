package nexters.payout.domain.dividend.infra;


import nexters.payout.domain.dividend.domain.Dividend;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DividendRepositoryCustom {

    Optional<Dividend> findByStockIdAndExDividendDate(UUID stockId, Instant date);
    List<Dividend> findAllByTickerAndYearAndMonth(String ticker, Integer year, Integer month);
    List<Dividend> findAllByTickerAndYear(String ticker, Integer year);
    void deleteByYearAndCreatedAt(Integer year, Instant createdAt);
}
