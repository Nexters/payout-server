package nexters.payout.apiserver.stock.application.dto.response;

import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.stock.domain.Stock;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;

public record StockDetailResponse(
        String ticker,
        String companyName,
        String sectorName,
        String exchange,
        String industry,
        Double price,
        Integer volume,
        Double dividendPerShare,
        LocalDate exDividendDate,
        LocalDate earliestPaymentDate,
        Double dividendYield,
        List<Month> dividendMonths
) {

    public static StockDetailResponse from(Stock stock) {
        return new StockDetailResponse(
                stock.getTicker(),
                stock.getName(),
                stock.getSector().getName(),
                stock.getExchange(),
                stock.getIndustry(),
                stock.getPrice(),
                stock.getVolume(),
                null,
                null,
                null,
                null,
                Collections.emptyList()
        );
    }

    public static StockDetailResponse of(Stock stock, Dividend dividend, List<Month> dividendMonths, Double dividendYield) {
        int thisYear = InstantProvider.getThisYear();
        return new StockDetailResponse(
                stock.getTicker(),
                stock.getName(),
                stock.getSector().getName(),
                stock.getExchange(),
                stock.getIndustry(),
                stock.getPrice(),
                stock.getVolume(),
                dividend.getDividend(),
                InstantProvider.toLocalDate(dividend.getExDividendDate()).withYear(thisYear),
                InstantProvider.toLocalDate(dividend.getPaymentDate()).withYear(thisYear),
                dividendYield,
                dividendMonths
        );
    }
}
