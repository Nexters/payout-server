package nexters.payout.apiserver.stock.application;

import lombok.RequiredArgsConstructor;
import nexters.payout.apiserver.stock.application.dto.request.SectorRatioRequest;
import nexters.payout.apiserver.stock.application.dto.request.TickerShare;
import nexters.payout.apiserver.stock.application.dto.response.SectorRatioResponse;
import nexters.payout.apiserver.stock.application.dto.response.StockDetailResponse;
import nexters.payout.core.exception.error.NotFoundException;
import nexters.payout.core.time.InstantTimeProvider;
import nexters.payout.domain.dividend.Dividend;
import nexters.payout.domain.dividend.repository.DividendRepository;
import nexters.payout.domain.stock.Sector;
import nexters.payout.domain.stock.Stock;
import nexters.payout.domain.stock.repository.StockRepository;
import nexters.payout.domain.stock.service.DividendAnalysisService;
import nexters.payout.domain.stock.service.SectorAnalysisService;
import nexters.payout.domain.stock.service.SectorAnalysisService.SectorInfo;
import nexters.payout.domain.stock.service.SectorAnalysisService.StockShare;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final DividendRepository dividendRepository;
    private final SectorAnalysisService sectorAnalysisService;
    private final DividendAnalysisService dividendAnalysisService;

    public StockDetailResponse getStockByTicker(String ticker) {
        Stock stock = stockRepository.findByTicker(ticker)
                .orElseThrow(() -> new NotFoundException(String.format("not found ticker [%s]", ticker)));

        List<Month> dividendMonths = dividendAnalysisService.calculateDividendMonths(
                stock, getDividends(stock.getId())
        );

        return findEarliestDividendThisYear(stock)
                .map(dividend -> StockDetailResponse.of(stock, dividend, dividendMonths))
                .orElseGet(() -> StockDetailResponse.from(stock));
    }

    private List<Dividend> getDividends(UUID stockId) {
        return dividendRepository.findAllByStockId(stockId);
    }

    private Optional<Dividend> findEarliestDividendThisYear(Stock stock) {
        int thisYear = InstantTimeProvider.getThisYear();

        return dividendRepository
                .findAllByStockIdAndPaymentDateYear(stock.getId(), thisYear, PageRequest.of(0, 1))
                .stream()
                .findFirst();
    }

    public List<SectorRatioResponse> analyzeSectorRatio(final SectorRatioRequest request) {
        List<StockShare> stockShares = getStockShares(request);
        Map<Sector, SectorInfo> sectorInfoMap = sectorAnalysisService.calculateSectorRatios(stockShares);

        return SectorRatioResponse.fromMap(sectorInfoMap);
    }

    private List<StockShare> getStockShares(final SectorRatioRequest request) {
        List<Stock> stocks = stockRepository.findAllByTickerIn(getTickers(request));
        Map<UUID, Dividend> stockDividendMap = getStockDividendMap(getStockIds(stocks));

        return stocks.stream()
                .map(stock -> new StockShare(
                        stock,
                        stockDividendMap.get(stock.getId()),
                        getTickerShareMap(request).get(stock.getTicker())))
                .collect(Collectors.toList());
    }

    private List<UUID> getStockIds(final List<Stock> stocks) {
        return stocks.stream()
                .map(Stock::getId)
                .toList();
    }

    private Map<UUID, Dividend> getStockDividendMap(final List<UUID> stockIds) {
        return dividendRepository.findAllByStockIdIn(stockIds)
                .stream()
                .collect(Collectors.groupingBy(Dividend::getStockId, getLatestDividendOrNull()));
    }

    private Collector<Dividend, Object, Dividend> getLatestDividendOrNull() {
        return Collectors.collectingAndThen(
                Collectors.maxBy(Comparator.comparing(Dividend::getDeclarationDate)),
                optionalDividend -> optionalDividend.orElse(null));
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
