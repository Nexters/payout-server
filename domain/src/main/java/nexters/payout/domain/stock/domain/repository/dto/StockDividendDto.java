package nexters.payout.domain.stock.domain.repository.dto;

import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.stock.domain.Stock;

public record StockDividendDto(
        Stock stock,
        Dividend dividend
) {
}
