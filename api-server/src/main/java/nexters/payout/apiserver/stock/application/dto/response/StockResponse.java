package nexters.payout.apiserver.stock.application.dto.response;

import nexters.payout.domain.dividend.Dividend;
import nexters.payout.domain.stock.Sector;
import nexters.payout.domain.stock.Stock;

import java.util.UUID;

public record StockResponse(
        UUID stockId,
        String ticker,
        String name,
        Sector sector,
        String exchange,
        String industry,
        Double price,
        Integer volume,
        Double dividend
) {
    public static StockResponse of(Stock stock, Dividend dividend) {
        return new StockResponse(
                stock.getId(),
                stock.getTicker(),
                stock.getName(),
                stock.getSector(),
                stock.getExchange(),
                stock.getIndustry(),
                stock.getPrice(),
                stock.getVolume(),
                dividend == null ? null : dividend.getDividend()
        );
    }
}
