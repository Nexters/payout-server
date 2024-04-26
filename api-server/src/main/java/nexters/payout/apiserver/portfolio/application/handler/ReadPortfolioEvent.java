package nexters.payout.apiserver.portfolio.application.handler;

import java.util.UUID;


public record ReadPortfolioEvent(
        UUID portfolioId
) {
}
