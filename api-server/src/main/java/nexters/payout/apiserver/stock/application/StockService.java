package nexters.payout.apiserver.stock.application;

import lombok.RequiredArgsConstructor;
import nexters.payout.apiserver.stock.application.dto.response.SectorRatioResponse;
import nexters.payout.domain.stock.service.SectorAnalyzer;
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
    private final SectorAnalyzer sectorAnalyzer;

    public List<SectorRatioResponse> findSectorRatios(final List<String> tickers) {
        List<Stock> stocks = stockRepository.findAllByTickerIn(tickers);
        Map<Sector, Double> sectorRatioMap = sectorAnalyzer.calculateSectorRatios(stocks);

        return SectorRatioResponse.fromMap(sectorRatioMap);
    }
}
