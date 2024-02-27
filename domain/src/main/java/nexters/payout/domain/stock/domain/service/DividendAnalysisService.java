package nexters.payout.domain.stock.domain.service;

import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.common.config.DomainService;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.stock.domain.Stock;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@DomainService
public class DividendAnalysisService {
    /**
     * 작년 데이터를 기반으로 배당을 주었던 월 리스트를 계산합니다.
     */
    public List<Month> calculateDividendMonths(final Stock stock, final List<Dividend> dividends) {
        int lastYear = InstantProvider.getLastYear();

        return dividends
                .stream()
                .filter(dividend -> stock.getId().equals(dividend.getStockId()))
                .map(dividend -> InstantProvider.toLocalDate(dividend.getExDividendDate()))
                .filter(exDividendDate -> exDividendDate.getYear() == lastYear)
                .map(LocalDate::getMonth)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 배당 수익률은 (연간 배당금 / 현재가) 를 기준으로 합니다.
     */
    public Double calculateDividendYield(final Stock stock, final List<Dividend> dividends) {
        double sumOfDividend = dividends
                .stream()
                .mapToDouble(Dividend::getDividend)
                .sum();

        Double stockPrice = stock.getPrice();

        if (stockPrice == null || stockPrice == 0) {
            return 0.0;
        }

        return sumOfDividend / stockPrice;
    }

    /**
     * 공시된 현재 연도의 데이터가 있는 경우 실제 지급일을 반환하고, 없으면 작년 데이터를 기반으로 가장 빠른 배당 지급일을 계산합니다.
     * 월과 일만 확인하기 때문에 과거 연도가 반환될 수 있습니다.
     */
    public Optional<Dividend> findUpcomingDividend(
            final List<Dividend> lastYearDividends, final List<Dividend> thisYearDividends
    ) {
        LocalDate now = InstantProvider.getNow();

        for (Dividend dividend : thisYearDividends) {
            LocalDate exDividendDate = InstantProvider.toLocalDate(dividend.getExDividendDate());
            if (exDividendDate.getYear() == now.getYear() && (isCurrentOrFutureDate(exDividendDate))) {
                return Optional.of(dividend);
            }
        }

        return lastYearDividends
                .stream()
                .map(dividend -> {
                    LocalDate exDividendDate = InstantProvider.toLocalDate(dividend.getExDividendDate());
                    LocalDate adjustedExDividendDate = exDividendDate.withYear(now.getYear());
                    return new AbstractMap.SimpleEntry<>(dividend, adjustedExDividendDate);
                })
                .filter(date -> isCurrentOrFutureDate(date.getValue()))
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    private boolean isCurrentOrFutureDate(final LocalDate date) {
        LocalDate now = InstantProvider.getNow();
        return date.isEqual(now) || date.isAfter(InstantProvider.getNow());
    }
}
