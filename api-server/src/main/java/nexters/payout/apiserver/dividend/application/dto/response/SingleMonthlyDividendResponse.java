package nexters.payout.apiserver.dividend.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.stock.domain.Stock;

public record SingleMonthlyDividendResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ticker")
        String ticker,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "logo url")
        String logoUrl,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "share")
        Integer share,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "dividend")
        Double dividend,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "total dividend")
        Double totalDividend
) {
    public static SingleMonthlyDividendResponse of(Stock stock, int share, Dividend dividend) {
        return new SingleMonthlyDividendResponse(
                stock.getTicker(),
                stock.getLogoUrl(),
                share,
                dividend.getDividend(),
                dividend.getDividend() * share
        );
    }
}
