package nexters.payout.apiserver.stock.application.dto.response;

import nexters.payout.domain.stock.Sector;
import nexters.payout.domain.stock.Stock;

public record StockResponse(
        String ticker,
        String name,
        Sector sector,
        String exchange,
        String industry,
        Double price,
        Integer volume) {
    public static StockResponse from(Stock stock) {
        return new StockResponse(
                stock.getTicker(),
                stock.getName(),
                stock.getSector(),
                stock.getExchange(),
                stock.getIndustry(),
                stock.getPrice(),
                stock.getVolume()
        );
    }
}
