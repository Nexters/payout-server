package nexters.payout.apiserver.dividend.application;

import lombok.RequiredArgsConstructor;
import nexters.payout.apiserver.stock.application.StockDividendQueryService;
import nexters.payout.apiserver.stock.application.dto.response.DividendResponse;
import nexters.payout.apiserver.stock.application.dto.response.StockDetailResponse;
import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.dividend.domain.repository.DividendRepository;
import nexters.payout.domain.stock.domain.Stock;
import nexters.payout.domain.stock.domain.exception.TickerNotFoundException;
import nexters.payout.domain.stock.domain.repository.StockRepository;
import nexters.payout.domain.stock.domain.service.StockDividendAnalysisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockDividendQueryServiceImpl implements StockDividendQueryService {

    private final StockDividendAnalysisService dividendAnalysisService;
    private final StockRepository stockRepository;
    private final DividendRepository dividendRepository;

    public StockDetailResponse getStockByTicker(final String ticker) {
        Stock stock = getStock(ticker);

        List<Dividend> lastYearDividends = getLastYearDividends(stock);
        List<Dividend> thisYearDividends = getThisYearDividends(stock);

        if (lastYearDividends.isEmpty() && thisYearDividends.isEmpty()) {
            return StockDetailResponse.of(stock, DividendResponse.noDividend());
        }

        List<Month> dividendMonths = dividendAnalysisService.calculateDividendMonths(stock, lastYearDividends);
        Double dividendYield = dividendAnalysisService.calculateDividendYield(stock, lastYearDividends);
        Double dividendPerShare = dividendAnalysisService.calculateAverageDividend(
                combinedDividends(lastYearDividends, thisYearDividends)
        );

        return dividendAnalysisService.findUpcomingDividend(lastYearDividends, thisYearDividends)
                .map(upcomingDividend -> StockDetailResponse.of(
                        stock,
                        DividendResponse.fullDividendInfo(upcomingDividend, dividendYield, dividendMonths)
                ))
                .orElse(StockDetailResponse.of(
                        stock,
                        DividendResponse.withoutDividendDates(dividendPerShare, dividendYield, dividendMonths)
                ));
    }

    private List<Dividend> combinedDividends(final List<Dividend> lastYearDividends, final List<Dividend> thisYearDividends) {
        return Stream.of(lastYearDividends, thisYearDividends)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private Stock getStock(final String ticker) {
        return stockRepository.findByTicker(ticker)
                .orElseThrow(() -> new TickerNotFoundException(ticker));
    }

    private List<Dividend> getLastYearDividends(final Stock stock) {
        int lastYear = InstantProvider.getLastYear();

        return dividendRepository.findAllByStockId(stock.getId())
                .stream()
                .filter(dividend -> InstantProvider.toLocalDate(dividend.getExDividendDate()).getYear() == lastYear)
                .collect(Collectors.toList());
    }

    private List<Dividend> getThisYearDividends(final Stock stock) {
        int thisYear = InstantProvider.getThisYear();

        return dividendRepository.findAllByStockId(stock.getId())
                .stream()
                .filter(dividend -> InstantProvider.toLocalDate(dividend.getExDividendDate()).getYear() == thisYear)
                .collect(Collectors.toList());
    }
}
