package nexters.payout.apiserver.portfolio.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexters.payout.apiserver.portfolio.application.dto.response.MonthlyDividendResponse;
import nexters.payout.apiserver.portfolio.application.dto.response.SingleYearlyDividendResponse;
import nexters.payout.apiserver.portfolio.application.dto.response.YearlyDividendResponse;
import nexters.payout.apiserver.portfolio.application.dto.response.SingleMonthlyDividendResponse;
import nexters.payout.apiserver.portfolio.application.dto.request.PortfolioRequest;
import nexters.payout.apiserver.portfolio.application.dto.response.PortfolioResponse;
import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.dividend.domain.repository.DividendRepository;
import nexters.payout.domain.portfolio.domain.Portfolio;
import nexters.payout.domain.portfolio.domain.PortfolioStock;
import nexters.payout.domain.portfolio.domain.exception.PortfolioNotFoundException;
import nexters.payout.domain.portfolio.domain.repository.PortfolioRepository;
import nexters.payout.domain.stock.domain.Stock;
import nexters.payout.domain.stock.domain.exception.StockIdNotFoundException;
import nexters.payout.domain.stock.domain.exception.TickerNotFoundException;
import nexters.payout.domain.stock.domain.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    public PortfolioResponse createPortfolio(final PortfolioRequest request) {

        List<PortfolioStock> portfolioStocks =
                request.tickerShares()
                        .stream().map(tickerShare -> new PortfolioStock(
                                stockRepository.findByTicker(tickerShare.ticker())
                                        .orElseThrow(() -> new TickerNotFoundException(tickerShare.ticker()))
                                        .getId(),
                                tickerShare.share()))
                        .toList();

        return new PortfolioResponse(portfolioRepository.save(
                new Portfolio(
                        InstantProvider.getExpireAt(),
                        portfolioStocks
                )).getId()
        );
    }

    @Transactional(readOnly = true)
    public List<MonthlyDividendResponse> getMonthlyDividends(final UUID id) {
        return InstantProvider.generateNext12Months()
                .stream()
                .map(yearMonth -> MonthlyDividendResponse.of(
                                yearMonth.getYear(),
                                yearMonth.getMonthValue(),
                                getDividendsOfLastYearAndMonth(
                                        portfolioRepository.findById(id)
                                                .orElseThrow(() -> new PortfolioNotFoundException(id))
                                                .getPortfolioStocks().getPortfolioStocks(),
                                        yearMonth.getMonthValue())
                        )
                )
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public YearlyDividendResponse getYearlyDividends(final UUID id) {

        List<SingleYearlyDividendResponse> dividends = portfolioRepository.findById(id)
                .orElseThrow(() -> new PortfolioNotFoundException(id))
                .getPortfolioStocks().getPortfolioStocks()
                .stream().map(portfolioStock -> {
                    Stock stock = stockRepository.findById(portfolioStock.getStockId())
                            .orElseThrow(() -> new StockIdNotFoundException(portfolioStock.getStockId()));
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
