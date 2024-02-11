package nexters.payout.apiserver.stock.application;

import lombok.RequiredArgsConstructor;
import nexters.payout.apiserver.stock.application.dto.request.SectorRatioRequest;
import nexters.payout.apiserver.stock.application.dto.request.TickerShare;
import nexters.payout.apiserver.stock.application.dto.response.SectorRatioResponse;
import nexters.payout.domain.dividend.Dividend;
import nexters.payout.domain.dividend.repository.DividendRepository;
import nexters.payout.domain.stock.Stock;
import nexters.payout.domain.stock.service.SectorAnalyzer;
import nexters.payout.domain.stock.service.SectorAnalyzer.StockShare;
import nexters.payout.domain.stock.service.SectorAnalyzer.SectorInfo;
import nexters.payout.domain.stock.Sector;
import nexters.payout.domain.stock.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final DividendRepository dividendRepository;
    private final SectorAnalyzer sectorAnalyzer;

    public List<SectorRatioResponse> findSectorRatios(final SectorRatioRequest request) {
        List<StockShare> stockShares = getStockShares(request);

        Map<Sector, SectorInfo> sectorInfoMap = sectorAnalyzer.calculateSectorRatios(stockShares);

        return SectorRatioResponse.fromMap(sectorInfoMap);
    }

    private List<StockShare> getStockShares(final SectorRatioRequest request) {
        List<Stock> stocks = stockRepository.findAllByTickerIn(getTickers(request));
        List<UUID> stockIds = stocks.stream()
                .map(Stock::getId)
                .toList();

        Map<UUID, Dividend> stockDividendMap = getStockDividendMap(stockIds);

        return stocks.stream()
                .map(stock -> new StockShare(
                        stock,
                        stockDividendMap.get(stock.getId()),
                        getTickerShareMap(request).get(stock.getTicker())))
                .collect(Collectors.toList());
    }

    private Map<UUID, Dividend> getStockDividendMap(List<UUID> stockIds) {
        return dividendRepository.findAllByStockIdIn(stockIds)
                .stream()
                .collect(Collectors.groupingBy(
                        Dividend::getStockId,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(Dividend::getDeclarationDate)),
                                optionalDividend -> optionalDividend.orElse(null))));
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
