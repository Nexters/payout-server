package nexters.payout.domain.stock.infra;

import nexters.payout.domain.stock.domain.Sector;
import nexters.payout.domain.stock.domain.Stock;
import nexters.payout.domain.stock.infra.dto.StockDividendDto;
import nexters.payout.domain.stock.infra.dto.StockDividendYieldDto;

import java.util.List;

public interface StockRepositoryCustom {

    List<Stock> findStocksByTickerOrNameWithPriority(String search, Integer pageNumber, Integer pageSize);
    List<StockDividendDto> findUpcomingDividendStock(Sector sector, int pageNumber, int pageSize);
    List<StockDividendYieldDto> findBiggestDividendYieldStock(int lastYear, Sector sector, int pageNumber, int pageSize);
}
