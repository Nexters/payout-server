package nexters.payout.batch.application;

import nexters.payout.batch.common.AbstractBatchServiceTest;
import nexters.payout.domain.PortfolioFixture;
import nexters.payout.domain.portfolio.domain.Portfolio;
import nexters.payout.domain.portfolio.domain.PortfolioStock;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static nexters.payout.domain.PortfolioFixture.STOCK_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PortfolioBatchServiceTest extends AbstractBatchServiceTest {

    @Test
    void 만료기간이_지난_포트폴리오는_삭제한다() {
        // given
        portfolioRepository.save(PortfolioFixture.createPortfolio(
                Instant.now().minus(1, ChronoUnit.DAYS),
                List.of(new PortfolioStock(STOCK_ID, 1))
        ));
        portfolioRepository.save(PortfolioFixture.createPortfolio(
                Instant.now().minus(2, ChronoUnit.DAYS),
                List.of(new PortfolioStock(STOCK_ID, 2))
        ));
        Portfolio notExpiredPortfolio = portfolioRepository.save(PortfolioFixture.createPortfolio(
                Instant.now().plus(1, ChronoUnit.DAYS),
                List.of(new PortfolioStock(STOCK_ID, 1))
        ));

        // when
        portfolioBatchService.deletePortfolio();

        // then
        List<Portfolio> actual = portfolioRepository.findAll();
        assertAll(
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(actual.get(0)).isEqualTo(notExpiredPortfolio)
        );
    }
}