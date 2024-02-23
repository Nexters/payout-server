package nexters.payout.domain.stock.infra;

import nexters.payout.domain.stock.domain.Stock;
import nexters.payout.domain.stock.domain.repository.dto.StockDividendDto;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface StockRepositoryCustom {

    List<Stock> findStocksByTickerOrNameWithPriority(String search, Integer pageNumber, Integer pageSize);
    List<StockDividendDto> findUpcomingDividendStock(int pageNumber, int pageSize);
}
