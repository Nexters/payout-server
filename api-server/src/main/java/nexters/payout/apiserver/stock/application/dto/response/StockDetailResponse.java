package nexters.payout.apiserver.stock.application.dto.response;

import nexters.payout.domain.dividend.Dividend;
import nexters.payout.domain.stock.Sector;
import nexters.payout.domain.stock.Stock;

import java.time.Instant;
import java.time.Month;
import java.util.Collections;
import java.util.List;

public record StockDetailResponse(
        String ticker,
        String name,
        Sector sector,
        String exchange,
        String industry,
        Double price,
        Integer volume,
        Double dividendPerShare,
        Instant exDividendDate,
        Instant earliestPaymentDate,
        Double dividendYield,
        List<Month> months
) {

    public static StockDetailResponse from(Stock stock) {
        return new StockDetailResponse(
                stock.getTicker(),
                stock.getName(),
                stock.getSector(),
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

    public static StockDetailResponse of(Stock stock, Dividend dividend, List<Month> dividendMonths) {
        return new StockDetailResponse(
                stock.getTicker(),
                stock.getName(),
                stock.getSector(),
                stock.getExchange(),
                stock.getIndustry(),
                stock.getPrice(),
                stock.getVolume(),
                dividend.getDividend(),
                dividend.getExDividendDate(),
                dividend.getPaymentDate(),
                stock.calculateDividendYield(dividend),
                dividendMonths
        );
    }
}
