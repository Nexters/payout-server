package nexters.payout.domain.stock.domain.repository;

import nexters.payout.domain.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, UUID> {
    Optional<Stock> findByTicker(String ticker);

    List<Stock> findAllByTickerIn(List<String> tickers);
}
