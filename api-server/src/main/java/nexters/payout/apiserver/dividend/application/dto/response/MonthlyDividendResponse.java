package nexters.payout.apiserver.dividend.application.dto.response;

import java.util.Comparator;
import java.util.List;

public record MonthlyDividendResponse(
        Integer year,
        Integer month,
        List<DividendResponse> dividends,
        Double totalDividend
) {
    public static MonthlyDividendResponse of(int year, int month, List<DividendResponse> dividends) {

        dividends = dividends.stream()
                .sorted(Comparator.comparingDouble(DividendResponse::totalDividend).reversed())
                .toList();
        return new MonthlyDividendResponse(
                year,
                month,
                dividends,
                dividends.stream()
                        .mapToDouble(DividendResponse::totalDividend)
                        .sum()
        );
    }
}
