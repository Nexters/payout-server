package nexters.payout.domain.stock.domain.exception;

import nexters.payout.core.exception.error.NotFoundException;

import java.util.UUID;

public class StockIdNotFoundException extends NotFoundException {

    public StockIdNotFoundException(UUID id) {
        super(String.format("not found stock id [%s]", id));
    }
}
