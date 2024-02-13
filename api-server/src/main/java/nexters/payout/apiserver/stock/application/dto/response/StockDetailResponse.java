package nexters.payout.apiserver.stock.application.dto.response;

import nexters.payout.domain.stock.Sector;

public record StockDetailResponse(
        String ticker,
        String name,
        Sector sector,
        String exchange,
        String industry,
        Double price,
        Integer volume,
        Double dividend

) {
}
