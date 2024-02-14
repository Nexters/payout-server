package nexters.payout.domain.stock.application.dto;

public record UpdateStockRequest(
        Double price,
        Integer volume
) {
}
