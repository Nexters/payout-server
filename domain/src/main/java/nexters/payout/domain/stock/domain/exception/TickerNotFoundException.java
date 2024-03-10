package nexters.payout.domain.stock.domain.exception;

import nexters.payout.core.exception.error.NotFoundException;

public class TickerNotFoundException extends NotFoundException {

    public TickerNotFoundException(String ticker) {
        super(String.format("not found ticker [%s]", ticker));
    }
}
