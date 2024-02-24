package nexters.payout.domain.stock.infra.dto;

import nexters.payout.domain.stock.domain.Stock;

public record StockDividendYieldDto(
        Stock stock,
        Double dividendYield
) {
}
