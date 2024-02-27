package nexters.payout.apiserver.dividend.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexters.payout.apiserver.dividend.application.dto.request.DividendRequest;
import nexters.payout.apiserver.dividend.application.dto.request.TickerShare;
import nexters.payout.apiserver.dividend.application.dto.response.SingleMonthlyDividendResponse;
import nexters.payout.apiserver.dividend.application.dto.response.MonthlyDividendResponse;
import nexters.payout.apiserver.dividend.application.dto.response.SingleYearlyDividendResponse;
import nexters.payout.apiserver.dividend.application.dto.response.YearlyDividendResponse;
import nexters.payout.core.exception.error.NotFoundException;
import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.dividend.domain.repository.DividendRepository;
import nexters.payout.domain.stock.domain.Stock;
import nexters.payout.domain.stock.domain.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DividendQueryService {

    private final DividendRepository dividendRepository;
    private final StockRepository stockRepository;

    public List<MonthlyDividendResponse> getMonthlyDividends(final DividendRequest request) {
        return InstantProvider.generateNext12Months()
                .stream()
                .map(yearMonth -> MonthlyDividendResponse.of(
                                yearMonth.getYear(),
                                yearMonth.getMonthValue(),
                                getDividendsOfLastYearAndMonth(request.tickerShares(), yearMonth.getMonthValue())
                        )
                )
                .collect(Collectors.toList());
    }

    public YearlyDividendResponse getYearlyDividends(final DividendRequest request) {
        List<SingleYearlyDividendResponse> dividends = request.tickerShares()
                .stream()
                .map(tickerShare -> {
                    String ticker = tickerShare.ticker();
                    return SingleYearlyDividendResponse.of(
                            getStock(ticker), tickerShare.share(), getYearlyDividend(ticker)
                    );
                })
                .filter(response -> response.totalDividend() != 0)
                .collect(Collectors.toList());

        return YearlyDividendResponse.of(dividends);
    }

    private double getYearlyDividend(final String ticker) {
        return getLastYearDividendsByTicker(ticker)
                .stream()
                .mapToDouble(Dividend::getDividend)
                .sum();
    }

    private List<Dividend> getLastYearDividendsByTicker(final String ticker) {
        return dividendRepository.findAllByTickerAndYear(ticker, InstantProvider.getLastYear());
    }

    private Stock getStock(final String ticker) {
        return stockRepository.findByTicker(ticker)
                .orElseThrow(() -> new NotFoundException(String.format("not found ticker [%s]", ticker)));
    }

    private List<SingleMonthlyDividendResponse> getDividendsOfLastYearAndMonth(
            final List<TickerShare> tickerShares, final int month
    ) {
        return tickerShares
                .stream()
                .flatMap(tickerShare -> stockRepository.findByTicker(tickerShare.ticker())
                        .map(stock -> getMonthlyDividendResponse(month, tickerShare, stock))
                        .orElseThrow(() -> new NotFoundException(String.format("not found ticker [%s]", tickerShare.ticker()))))
                .toList();
    }

    private Stream<SingleMonthlyDividendResponse> getMonthlyDividendResponse(
            final int month, final TickerShare tickerShare, final Stock stock
    ) {
        return getLastYearDividendsByTickerAndMonth(tickerShare.ticker(), month)
                .stream()
                .map(dividend -> SingleMonthlyDividendResponse.of(stock, tickerShare.share(), dividend));
    }

    private List<Dividend> getLastYearDividendsByTickerAndMonth(final String ticker, final int month) {
        return dividendRepository.findAllByTickerAndYearAndMonth(ticker, InstantProvider.getLastYear(), month);
    }
}
