package nexters.payout.apiserver.dividend.application.dto.response;

import java.util.Comparator;
import java.util.List;

public record YearlyDividendResponse(
        List<SingleYearlyDividendResponse> dividends,
        Double totalDividend
) {
    public static YearlyDividendResponse of(List<SingleYearlyDividendResponse> dividends) {

        dividends = dividends.stream()
                .sorted(Comparator.comparingDouble(SingleYearlyDividendResponse::totalDividend).reversed())
                .toList();
        return new YearlyDividendResponse(
                dividends,
                dividends.stream()
                        .mapToDouble(SingleYearlyDividendResponse::totalDividend)
                        .sum()
        );
    }
}
