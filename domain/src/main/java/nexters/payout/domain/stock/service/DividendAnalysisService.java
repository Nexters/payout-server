package nexters.payout.domain.stock.service;

import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.common.config.DomainService;
import nexters.payout.domain.dividend.Dividend;
import nexters.payout.domain.stock.Stock;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

@DomainService
public class DividendAnalysisService {
    /**
     * 작년 1월 ~ 12월을 기준으로 배당을 주었던 월 리스트를 계산합니다.
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

    public Double calculateDividendYield(final Stock stock, final List<Dividend> dividends) {
        double sumOfDividend = dividends.stream().mapToDouble(Dividend::getDividend).sum();
        Double stockPrice = stock.getPrice();

        if (stockPrice == null || stockPrice == 0) {
            return 0.0;
        }

        return sumOfDividend / stockPrice;
    }
}
