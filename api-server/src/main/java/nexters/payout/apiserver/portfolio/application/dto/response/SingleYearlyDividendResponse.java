package nexters.payout.apiserver.portfolio.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import nexters.payout.domain.stock.domain.Stock;

public record SingleYearlyDividendResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String ticker,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String logoUrl,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Integer share,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Double totalDividend
) {
    public static SingleYearlyDividendResponse of(Stock stock, int share, double dividend) {
        return new SingleYearlyDividendResponse(
                stock.getTicker(),
                stock.getLogoUrl(),
                share,
                dividend * share
        );
    }
}
