package nexters.payout.apiserver.stock.application.dto.request;

public record TickerShare(
        String ticker,
        Integer share
) {
    
}
