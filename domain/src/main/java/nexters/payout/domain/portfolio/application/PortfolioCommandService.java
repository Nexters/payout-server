package nexters.payout.domain.portfolio.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nexters.payout.domain.portfolio.domain.Portfolio;
import nexters.payout.domain.portfolio.domain.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PortfolioCommandService {

    private final PortfolioRepository portfolioRepository;

    public UUID createPortfolio(Portfolio portfolio) {
        return portfolioRepository.save(portfolio).getId();
    }
}
