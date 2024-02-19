package nexters.payout.domain.stock.domain.service;

import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.common.config.DomainService;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.stock.domain.Stock;

import java.time.LocalDate;
import java.time.Month;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@DomainService
public class DividendAnalysisService {
    /**
     * 작년 데이터를 기반으로 배당을 주었던 월 리스트를 계산합니다.
     */
    public List<Month> calculateDividendMonths(final Stock stock, final List<Dividend> dividends) {
        int lastYear = InstantProvider.getLastYear();

        return dividends.stream()
                .filter(dividend -> stock.getId().equals(dividend.getStockId()))
                .map(dividend -> InstantProvider.toLocalDate(dividend.getPaymentDate()))
                .filter(localDate -> localDate.getYear() == lastYear)
                .map(LocalDate::getMonth)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 배당 수익률은 (연간 배당금 / 현재가) 를 기준으로 합니다.
     */
    public Double calculateDividendYield(final Stock stock, final List<Dividend> dividends) {
        double sumOfDividend = dividends.stream()
                .mapToDouble(Dividend::getDividend)
                .sum();

        Double stockPrice = stock.getPrice();

        if (stockPrice == null || stockPrice == 0) {
            return 0.0;
        }

        return sumOfDividend / stockPrice;
    }

    /**
     * 작년 데이터를 기반으로 가장 빠른 배당 지급일을 계산합니다.
     */
    public Optional<Dividend> findEarliestDividendThisYear(final List<Dividend> lastYearDividends) {
        int thisYear = InstantProvider.getThisYear();

        return lastYearDividends
                .stream()
                .map(dividend -> {
                    LocalDate paymentDate = InstantProvider.toLocalDate(dividend.getPaymentDate());
                    LocalDate adjustedPaymentDate = paymentDate.withYear(thisYear);
                    return new AbstractMap.SimpleEntry<>(dividend, adjustedPaymentDate);
                })
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }
}
