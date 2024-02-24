package nexters.payout.apiserver.dividend.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Comparator;
import java.util.List;

public record MonthlyDividendResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "year")
        Integer year,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "month")
        Integer month,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "dividends")
        List<SingleMonthlyDividendResponse> dividends,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "total dividend")
        Double totalDividend
) {
    public static MonthlyDividendResponse of(int year, int month, List<SingleMonthlyDividendResponse> dividends) {

        dividends = dividends
                .stream()
                .sorted(Comparator.comparingDouble(SingleMonthlyDividendResponse::totalDividend).reversed())
                .toList();
        return new MonthlyDividendResponse(
                year,
                month,
                dividends,
                dividends
                        .stream()
                        .mapToDouble(SingleMonthlyDividendResponse::totalDividend)
                        .sum()
        );
    }
}
