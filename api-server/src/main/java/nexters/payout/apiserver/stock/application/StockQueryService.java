package nexters.payout.apiserver.stock.application;

import lombok.RequiredArgsConstructor;
import nexters.payout.apiserver.stock.application.dto.request.SectorRatioRequest;
import nexters.payout.apiserver.stock.application.dto.request.TickerShare;
import nexters.payout.apiserver.stock.application.dto.response.*;
import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.stock.domain.Sector;
import nexters.payout.domain.stock.domain.Stock;
import nexters.payout.domain.stock.domain.repository.StockRepository;
import nexters.payout.domain.stock.domain.service.SectorAnalysisService;
import nexters.payout.domain.stock.domain.service.SectorAnalysisService.SectorInfo;
import nexters.payout.domain.stock.domain.service.SectorAnalysisService.StockShare;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockQueryService {

    private final StockRepository stockRepository;
    private final SectorAnalysisService sectorAnalysisService;
    private final StockDividendQueryService stockDividendQueryService;

    public List<StockResponse> searchStock(final String keyword, final Integer pageNumber, final Integer pageSize) {
        return stockRepository.findStocksByTickerOrNameWithPriority(keyword, pageNumber, pageSize)
                .stream()
                .map(StockResponse::from)
                .collect(Collectors.toList());
    }

    public StockDetailResponse getStockByTicker(final String ticker) {
        return stockDividendQueryService.getStockByTicker(ticker);
    }

    public List<SectorRatioResponse> analyzeSectorRatio(final SectorRatioRequest request) {
        List<StockShare> stockShares = getStockShares(request);

        Map<Sector, SectorInfo> sectorInfoMap = sectorAnalysisService.calculateSectorRatios(stockShares);

        return SectorRatioResponse.fromMap(sectorInfoMap);
    }

    public UpcomingDividendResponse getUpcomingDividendStocks(final Sector sector, final int pageNumber, final int pageSize) {
        return UpcomingDividendResponse.of(
                stockRepository.findUpcomingDividendStock(sector, pageNumber, pageSize)
                        .stream()
                        .map(stockDividend -> SingleUpcomingDividendResponse.of(
                                stockDividend.stock(),
                                stockDividend.dividend())
                        )
                        .collect(Collectors.toList())
        );
    }

    public StockDividendYieldResponse getBiggestDividendStocks(final Sector sector, final int pageNumber, final int pageSize) {
        return StockDividendYieldResponse.of(
                stockRepository.findBiggestDividendYieldStock(InstantProvider.getLastYear(), sector, pageNumber, pageSize)
                        .stream()
                        .map(stockDividendYield -> SingleStockDividendYieldResponse.of(
                                stockDividendYield.stock(),
                                stockDividendYield.dividendYield())
                        )
                        .collect(Collectors.toList())
        );
    }

    private List<StockShare> getStockShares(final SectorRatioRequest request) {
        List<Stock> stocks = stockRepository.findAllByTickerIn(getTickers(request));

        return stocks
                .stream()
                .map(stock -> new StockShare(
                        stock,
                        getTickerShareMap(request).get(stock.getTicker())))
                .collect(Collectors.toList());
    }

    private List<String> getTickers(final SectorRatioRequest request) {
        return request.tickerShares()
                .stream()
                .map(TickerShare::ticker)
                .collect(Collectors.toList());
    }

    private Map<String, Integer> getTickerShareMap(final SectorRatioRequest request) {
        return request.tickerShares()
                .stream()
                .collect(Collectors.toMap(TickerShare::ticker, TickerShare::share));
    }
}
