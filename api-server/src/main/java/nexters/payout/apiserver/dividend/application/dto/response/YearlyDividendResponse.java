package nexters.payout.apiserver.dividend.application.dto.response;

import java.util.Comparator;
import java.util.List;

public record YearlyDividendResponse(
        List<DividendResponse> dividends,
        Double totalDividend
) {
    public static YearlyDividendResponse of(List<DividendResponse> dividends) {

        dividends = dividends.stream()
                .sorted(Comparator.comparingDouble(DividendResponse::totalDividend).reversed())
                .toList();
        return new YearlyDividendResponse(
                dividends,
                dividends.stream()
                        .mapToDouble(DividendResponse::totalDividend)
                        .sum()
        );
    }
}
