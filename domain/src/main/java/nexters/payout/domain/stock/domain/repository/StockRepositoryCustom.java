package nexters.payout.domain.stock.domain.repository;

import nexters.payout.domain.stock.domain.Stock;
import nexters.payout.domain.stock.domain.repository.dto.StockDividendDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepositoryCustom {

    List<Stock> findStocksByTickerOrNameWithPriority(String search, Integer pageNumber, Integer pageSize);
    List<StockDividendDto> findUpcomingDividendStock(int pageNumber, int pageSize);
}
