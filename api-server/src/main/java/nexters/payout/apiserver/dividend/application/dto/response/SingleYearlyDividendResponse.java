package nexters.payout.apiserver.dividend.application.dto.response;

import nexters.payout.domain.stock.domain.Stock;

public record SingleYearlyDividendResponse(
        String ticker,
        String logoUrl,
        Integer share,
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
