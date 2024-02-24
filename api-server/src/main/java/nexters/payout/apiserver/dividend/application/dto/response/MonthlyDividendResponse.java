package nexters.payout.apiserver.dividend.application.dto.response;

import java.util.Comparator;
import java.util.List;

public record MonthlyDividendResponse(
        Integer year,
        Integer month,
        List<SingleMonthlyDividendResponse> dividends,
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
