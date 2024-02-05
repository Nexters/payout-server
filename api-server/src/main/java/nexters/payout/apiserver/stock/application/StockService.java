package nexters.payout.apiserver.stock.application;

import lombok.RequiredArgsConstructor;
import nexters.payout.apiserver.stock.application.dto.response.SectorRatioResponse;
import nexters.payout.domain.stock.PortfolioService;
import nexters.payout.domain.stock.Sector;
import nexters.payout.domain.stock.Stock;
import nexters.payout.domain.stock.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final PortfolioService portfolioService;

    public List<SectorRatioResponse> findSectorRatios(List<String> tickers) {
        List<Stock> stocks = stockRepository.findAllByTickerIn(tickers);

        Map<Sector, Double> sectorRatioMap = portfolioService.calculateSectorRatios(stocks);

        return SectorRatioResponse.createResponseList(sectorRatioMap);
    }
}
