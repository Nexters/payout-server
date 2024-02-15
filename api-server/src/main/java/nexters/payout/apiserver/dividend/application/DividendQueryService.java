package nexters.payout.apiserver.dividend.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexters.payout.apiserver.dividend.application.dto.request.DividendRequest;
import nexters.payout.apiserver.dividend.application.dto.response.DividendResponse;
import nexters.payout.apiserver.dividend.application.dto.response.MonthlyDividendResponse;
import nexters.payout.apiserver.dividend.application.dto.response.YearlyDividendResponse;
import nexters.payout.apiserver.dividend.infra.eodhd.EodhdProperties;
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

    private final DividendRepository dividendRepository;
    private final StockRepository stockRepository;
    public final EodhdProperties eodhdProperties;

    /**
     * 사용자가 추가한 주식의 예상 월간 배당금을 반환하는 메서드입니다.
     * TODO: 작년 1월 ~ 12월 기준으로 예상 배당금을 반환하도록 설정
     *
     * @param request 사용자가 추가한 주식
     * @return 예상 월간 배당금 정보
     */
    public List<MonthlyDividendResponse> getMonthlyDividends(final DividendRequest request) {

        return IntStream.rangeClosed(1, 12)
                .mapToObj(month -> MonthlyDividendResponse.of(
                        InstantProvider.getNextYear(),
                        month,
                        getDividendsOfLastYear(request, month)))
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 추가한 주식의 예상 연간 배당금을 반환하는 메서드입니다.
     * TODO: 작년 1월 ~ 12월 기준으로 예상 배당금을 반환하도록 설정
     *
     * @param request 사용자가 추가한 주식
     * @return 예상 연간 배당금 정보
     */
    public YearlyDividendResponse getYearlyDividends(final DividendRequest request) {

        return YearlyDividendResponse.of(
                IntStream.rangeClosed(1, 12)
                        .mapToObj(month -> getDividendsOfLastYear(request, month))
                        .flatMap(List::stream)
                        .collect(Collectors.toList()));
    }

    private List<DividendResponse> getDividendsOfLastYear(final DividendRequest request, int month) {

        return request.tickerShares().stream()
                .flatMap(tickerShare -> {
                    List<Dividend> findDividends
                            = dividendRepository.findAllByTickerAndYearAndMonth(
                            tickerShare.ticker(),
                            InstantProvider.getLastYear(),
                            month);

                    return stockRepository.findByTicker(tickerShare.ticker())
                            .map(stock -> findDividends.stream()
                                    .map(dividend -> DividendResponse.of(
                                            stock,
                                            generateLogoUrl(stock.getTicker()),
                                            tickerShare.share(),
                                            dividend)))
                            .orElseThrow(() -> new NotFoundException(String.format("not found ticker [%s]", tickerShare.ticker())));
                })
                .toList();
    }

    private String generateLogoUrl(String ticker) {
        return eodhdProperties.getBaseUrl() + eodhdProperties.getStockLogoPath() + ticker + eodhdProperties.getImagePostfix();
    }
}
