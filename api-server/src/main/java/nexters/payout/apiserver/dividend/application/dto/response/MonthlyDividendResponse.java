package nexters.payout.apiserver.dividend.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Comparator;
import java.util.List;

public record MonthlyDividendResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Integer year,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Integer month,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<SingleMonthlyDividendResponse> dividends,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Double totalDividend
) {
    public static MonthlyDividendResponse of(
            final int year, final int month, final List<SingleMonthlyDividendResponse> dividends
    ) {
        return new MonthlyDividendResponse(
                year,
                month,
                dividends.stream()
                        .sorted(Comparator.comparingDouble(SingleMonthlyDividendResponse::totalDividend).reversed())
                        .toList(),
                dividends.stream()
                        .mapToDouble(SingleMonthlyDividendResponse::totalDividend)
                        .sum()
        );
    }
}
