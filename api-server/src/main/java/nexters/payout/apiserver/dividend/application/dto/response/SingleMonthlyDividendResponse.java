package nexters.payout.apiserver.dividend.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.stock.domain.Stock;

public record SingleMonthlyDividendResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String ticker,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String logoUrl,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Integer share,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Double dividend,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
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
