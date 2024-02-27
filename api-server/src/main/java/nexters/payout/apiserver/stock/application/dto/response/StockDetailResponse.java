package nexters.payout.apiserver.stock.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.stock.domain.Stock;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record StockDetailResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID stockId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String ticker,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String companyName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String sectorName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String exchange,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String industry,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Double price,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Integer volume,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String logoUrl,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Double dividendPerShare,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate exDividendDate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate earliestPaymentDate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Double dividendYield,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<Month> dividendMonths
) {

    public static StockDetailResponse from(Stock stock) {
        return new StockDetailResponse(
                stock.getId(),
                stock.getTicker(),
                stock.getName(),
                stock.getSector().getName(),
                stock.getExchange(),
                stock.getIndustry(),
                stock.getPrice(),
                stock.getVolume(),
                stock.getLogoUrl(),
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
                stock.getId(),
                stock.getTicker(),
                stock.getName(),
                stock.getSector().getName(),
                stock.getExchange(),
                stock.getIndustry(),
                stock.getPrice(),
                stock.getVolume(),
                stock.getLogoUrl(),
                dividend.getDividend(),
                InstantProvider.toLocalDate(dividend.getExDividendDate()).withYear(thisYear),
                dividend.getPaymentDate() == null ? null :
                        InstantProvider.toLocalDate(dividend.getPaymentDate()).withYear(thisYear),
                dividendYield,
                dividendMonths
        );
    }
}
