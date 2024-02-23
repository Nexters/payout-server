package nexters.payout.apiserver.stock.application.dto.response;

import nexters.payout.domain.stock.domain.Stock;

import java.util.UUID;

public record StockDividendYieldResponse(
        UUID stockId,
        String ticker,
        String logoUrl,
        Double dividendYield
) {

    public static StockDividendYieldResponse of(Stock stock, Double dividendYield) {
        return new StockDividendYieldResponse(
                stock.getId(),
                stock.getTicker(),
                stock.getLogoUrl(),
                dividendYield
        );
    }
}
