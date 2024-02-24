package nexters.payout.apiserver.dividend.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Comparator;
import java.util.List;

public record YearlyDividendResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "dividends")
        List<SingleYearlyDividendResponse> dividends,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "total dividend")
        Double totalDividend
) {
    public static YearlyDividendResponse of(List<SingleYearlyDividendResponse> dividends) {

        dividends = dividends
                .stream()
                .sorted(Comparator.comparingDouble(SingleYearlyDividendResponse::totalDividend).reversed())
                .toList();
        return new YearlyDividendResponse(
                dividends,
                dividends
                        .stream()
                        .mapToDouble(SingleYearlyDividendResponse::totalDividend)
                        .sum()
        );
    }
}
