package nexters.payout.apiserver.portfolio.application.handler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nexters.payout.domain.portfolio.domain.Portfolio;
import nexters.payout.domain.portfolio.domain.exception.PortfolioNotFoundException;
import nexters.payout.domain.portfolio.domain.repository.PortfolioRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Transactional
public class PortfolioEventHandler {

    private final PortfolioRepository portfolioRepository;

    void handleReadPortfolioEvent(final ReadPortfolioEvent event) {
        Portfolio portfolio = portfolioRepository.findById(event.portfolioId())
                .orElseThrow(() -> new PortfolioNotFoundException(event.portfolioId()));
        portfolio.incrementHits();
    }
}
