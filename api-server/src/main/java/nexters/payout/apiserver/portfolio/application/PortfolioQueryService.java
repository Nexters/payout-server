package nexters.payout.apiserver.portfolio.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexters.payout.apiserver.portfolio.application.dto.request.PortfolioRequest;
import nexters.payout.apiserver.portfolio.application.dto.response.*;
import nexters.payout.apiserver.stock.application.dto.response.SectorRatioResponse;
import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.dividend.domain.repository.DividendRepository;
import nexters.payout.domain.portfolio.domain.Portfolio;
import nexters.payout.domain.portfolio.domain.PortfolioStock;
import nexters.payout.domain.portfolio.domain.exception.PortfolioNotFoundException;
import nexters.payout.domain.portfolio.domain.repository.PortfolioRepository;
import nexters.payout.domain.stock.domain.Sector;
import nexters.payout.domain.stock.domain.Stock;
import nexters.payout.domain.stock.domain.exception.StockIdNotFoundException;
import nexters.payout.domain.stock.domain.exception.TickerNotFoundException;
import nexters.payout.domain.stock.domain.repository.StockRepository;
import nexters.payout.domain.stock.domain.service.SectorAnalysisService;
import nexters.payout.domain.stock.domain.service.SectorAnalysisService.SectorInfo;
import nexters.payout.domain.stock.domain.service.SectorAnalysisService.StockShare;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PortfolioQueryService {

    private final StockRepository stockRepository;
    private final PortfolioRepository portfolioRepository;
    private final DividendRepository dividendRepository;
    private final SectorAnalysisService sectorAnalysisService;

    public PortfolioResponse createPortfolio(final PortfolioRequest request) {

        List<PortfolioStock> portfolioStocks =
                request.tickerShares()
                        .stream()
                        .map(it -> new PortfolioStock(getStockByTicker(it.ticker()).getId(), it.share()))
                        .toList();

        return new PortfolioResponse(
                portfolioRepository.save(new Portfolio(InstantProvider.getExpireAt(), portfolioStocks))
                        .getId()
        );
    }

    @Transactional(readOnly = true)
    public List<SectorRatioResponse> analyzeSectorRatio(final UUID portfolioId) {
        List<PortfolioStock> portfolioStocks = getPortfolio(portfolioId).portfolioStocks();
        List<StockShare> stockShares = portfolioStocks
                .stream()
                .map(ps -> new StockShare(getStock(ps.getStockId()), ps.getShares()))
                .toList();
        Map<Sector, SectorInfo> sectorInfoMap = sectorAnalysisService.calculateSectorRatios(stockShares);
        return SectorRatioResponse.fromMap(sectorInfoMap);
    }

    @Transactional(readOnly = true)
    public List<MonthlyDividendResponse> getMonthlyDividends(final UUID id) {
        return InstantProvider.generateNext12Months()
                .stream()
                .map(yearMonth -> MonthlyDividendResponse.of(
                                yearMonth.getYear(),
                                yearMonth.getMonthValue(),
                                getDividendsOfLastYearAndMonth(
                                        getPortfolio(id).portfolioStocks(),
                                        yearMonth.getMonthValue()
                                )
                        )
                )
                .collect(Collectors.toList());
    }

    private Stock getStockByTicker(String ticker) {
        return stockRepository.findByTicker(ticker)
                .orElseThrow(() -> new TickerNotFoundException(ticker));
    }

    private Stock getStock(UUID stockId) {
        return stockRepository.findById(stockId).orElseThrow(() -> new StockIdNotFoundException(stockId));
    }

    private Portfolio getPortfolio(UUID id) {
        return portfolioRepository.findById(id)
                .orElseThrow(() -> new PortfolioNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public YearlyDividendResponse getYearlyDividends(final UUID id) {

        List<SingleYearlyDividendResponse> dividends = getPortfolio(id)
                .portfolioStocks()
                .stream()
                .map(portfolioStock -> {
                    Stock stock = getStock(portfolioStock.getStockId());
                    return SingleYearlyDividendResponse.of(
                            stock, portfolioStock.getShares(), getYearlyDividend(stock.getId())
                    );
                })
                .filter(response -> response.totalDividend() != 0)
                .toList();

        return YearlyDividendResponse.of(dividends);
    }

    private double getYearlyDividend(final UUID stockId) {
        return getLastYearDividendsByStockId(stockId)
                .stream()
                .mapToDouble(Dividend::getDividend)
                .sum();
    }

    private List<Dividend> getLastYearDividendsByStockId(final UUID id) {
        return dividendRepository.findAllByIdAndYear(id, InstantProvider.getLastYear());
    }

    private List<SingleMonthlyDividendResponse> getDividendsOfLastYearAndMonth(
            final List<PortfolioStock> portfolioStocks, final int month
    ) {
        return portfolioStocks
                .stream()
                .flatMap(portfolioStock -> stockRepository.findById(portfolioStock.getStockId())
                        .map(stock -> getMonthlyDividendResponse(month, portfolioStock, stock))
                        .orElseThrow(() -> new StockIdNotFoundException(portfolioStock.getStockId())))
                .toList();
    }

    private Stream<SingleMonthlyDividendResponse> getMonthlyDividendResponse(
            final int month, final PortfolioStock portfolioStock, final Stock stock
    ) {
        return getLastYearDividendsByStockIdAndMonth(portfolioStock.getStockId(), month)
                .stream()
                .map(dividend -> SingleMonthlyDividendResponse.of(stock, portfolioStock.getShares(), dividend));
    }

    private List<Dividend> getLastYearDividendsByStockIdAndMonth(final UUID stockId, final int month) {
        return dividendRepository.findAllByIdAndYearAndMonth(stockId, InstantProvider.getLastYear(), month);
    }
}
