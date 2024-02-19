package nexters.payout.apiserver.dividend.application.dto.response;

import nexters.payout.domain.stock.domain.Stock;

public record SingleYearlyDividendResponse(
        String ticker,
        String logoUrl,
        Integer share,
        Double totalDividend
) {
    public static SingleYearlyDividendResponse of(Stock stock, String logoUrl, int share, double dividend) {
        return new SingleYearlyDividendResponse(
                stock.getTicker(),
                logoUrl,
                share,
                dividend * share
        );
    }
}
