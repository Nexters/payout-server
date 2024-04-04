package nexters.payout.batch.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexters.payout.domain.portfolio.domain.Portfolio;
import nexters.payout.domain.portfolio.domain.repository.PortfolioRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PortfolioBatchService {

    private final PortfolioRepository portfolioRepository;

    @Scheduled(cron = "${schedules.cron.portfolio}", zone = "UTC")
    void deletePortfolio() {
        log.info("delete portfolio start..");
        portfolioRepository.deleteAllById(getExpiredPortfolioIds());
        log.info("delete portfolio end..");
    }

    private List<UUID> getExpiredPortfolioIds() {
        return portfolioRepository.findByExpireAtBefore(Instant.now())
                .stream()
                .map(Portfolio::getId)
                .toList();
    }
}
