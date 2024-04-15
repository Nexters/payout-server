package nexters.payout.domain.portfolio.domain.exception;

import nexters.payout.core.exception.error.NotFoundException;

import java.util.UUID;

public class PortfolioNotFoundException extends NotFoundException {

    public PortfolioNotFoundException(UUID id) {
        super(String.format("not found portfolio [%s]", id));
    }
}
