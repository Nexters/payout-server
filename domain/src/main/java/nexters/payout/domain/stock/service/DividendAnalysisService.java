package nexters.payout.domain.stock.service;

import nexters.payout.core.time.InstantTimeProvider;
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
     *
     * @param stock
     * @param dividends
     * @return Month List
     */
    public List<Month> calculateDividendMonths(Stock stock, List<Dividend> dividends) {
        int lastYear = InstantTimeProvider.getLastYear();

        return dividends.stream()
                .filter(dividend -> stock.getId().equals(dividend.getStockId()))
                .map(dividend -> InstantTimeProvider.toLocalDate(dividend.getPaymentDate()))
                .filter(localDate -> localDate.getYear() == lastYear)
                .map(LocalDate::getMonth)
                .distinct()
                .collect(Collectors.toList());
    }
}
