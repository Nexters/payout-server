package nexters.payout.apiserver.dividend.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexters.payout.apiserver.dividend.application.dto.request.DividendRequest;
import nexters.payout.apiserver.dividend.application.dto.response.SingleMonthlyDividendResponse;
import nexters.payout.apiserver.dividend.application.dto.response.MonthlyDividendResponse;
import nexters.payout.apiserver.dividend.application.dto.response.SingleYearlyDividendResponse;
import nexters.payout.apiserver.dividend.application.dto.response.YearlyDividendResponse;
import nexters.payout.core.exception.error.NotFoundException;
import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.dividend.domain.repository.DividendRepository;
import nexters.payout.domain.stock.domain.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DividendQueryService {

    private final Integer JANUARY = 1;
    private final Integer DECEMBER = 12;
    private final DividendRepository dividendRepository;
    private final StockRepository stockRepository;

    public List<MonthlyDividendResponse> getMonthlyDividends(final DividendRequest request) {

        return IntStream.rangeClosed(JANUARY, DECEMBER)
                .mapToObj(month -> MonthlyDividendResponse.of(
                        InstantProvider.getNextYear(),
                        month,
                        getDividendsOfLastYearAndMonth(request, month)))
                .collect(Collectors.toList());
    }

    public YearlyDividendResponse getYearlyDividends(final DividendRequest request) {

        List<SingleYearlyDividendResponse> dividends = request.tickerShares().stream()
                .map(tickerShare -> {
                    String ticker = tickerShare.ticker();
                    List<Dividend> findDividends = dividendRepository.findAllByTickerAndYear(ticker, InstantProvider.getLastYear());
                    return SingleYearlyDividendResponse.of(
                            stockRepository.findByTicker(ticker)
                                    .orElseThrow(() -> new NotFoundException(String.format("not found ticker [%s]", tickerShare.ticker()))),
                            tickerShare.share(),
                            findDividends.stream().mapToDouble(Dividend::getDividend).sum()
                    );
                })
                .filter(response -> response.totalDividend() != 0)
                .collect(Collectors.toList());

        return YearlyDividendResponse.of(dividends);
    }

    private List<SingleMonthlyDividendResponse> getDividendsOfLastYearAndMonth(final DividendRequest request, int month) {

        return request.tickerShares().stream()
                .flatMap(tickerShare -> {
                    List<Dividend> findDividends
                            = dividendRepository.findAllByTickerAndYearAndMonth(
                            tickerShare.ticker(),
                            InstantProvider.getLastYear(),
                            month);

                    return stockRepository.findByTicker(tickerShare.ticker())
                            .map(stock -> findDividends.stream()
                                    .map(dividend -> SingleMonthlyDividendResponse.of(
                                            stock,
                                            tickerShare.share(),
                                            dividend)))
                            .orElseThrow(() -> new NotFoundException(String.format("not found ticker [%s]", tickerShare.ticker())));
                })
                .toList();
    }
}
