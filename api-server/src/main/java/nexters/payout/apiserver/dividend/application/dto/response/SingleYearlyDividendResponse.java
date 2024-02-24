package nexters.payout.apiserver.dividend.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import nexters.payout.domain.stock.domain.Stock;

public record SingleYearlyDividendResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ticker")
        String ticker,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "logo url")
        String logoUrl,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "share")
        Integer share,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "total dividend")
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
