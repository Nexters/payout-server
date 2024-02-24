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
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ticker and share")
        UUID stockId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ticker name")
        String ticker,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "company name")
        String companyName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "sector name")
        String sectorName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "exchange")
        String exchange,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "industry")
        String industry,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "price")
        Double price,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "volume")
        Integer volume,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "logo url")
        String logoUrl,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "dividend per share")
        Double dividendPerShare,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ex dividend date")
        LocalDate exDividendDate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "earliest payment date")
        LocalDate earliestPaymentDate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "dividend yield")
        Double dividendYield,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "dividend months")
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
                InstantProvider.toLocalDate(dividend.getPaymentDate()).withYear(thisYear),
                dividendYield,
                dividendMonths
        );
    }
}
