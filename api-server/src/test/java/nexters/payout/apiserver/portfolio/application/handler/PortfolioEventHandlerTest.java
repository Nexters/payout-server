package nexters.payout.apiserver.portfolio.application.handler;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import nexters.payout.apiserver.portfolio.common.IntegrationTest;
import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.DividendFixture;
import nexters.payout.domain.PortfolioFixture;
import nexters.payout.domain.StockFixture;
import nexters.payout.domain.portfolio.domain.Portfolio;
import nexters.payout.domain.portfolio.domain.PortfolioStock;
import nexters.payout.domain.stock.domain.Sector;
import nexters.payout.domain.stock.domain.Stock;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static nexters.payout.domain.StockFixture.AAPL;
import static nexters.payout.domain.StockFixture.TSLA;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

class PortfolioEventHandlerTest extends IntegrationTest {

    @Test
    void 포트폴리오_조회시_조회수가_늘어난다() {
        // given
        Portfolio portfolio = stockAndDividendAndPortfolioGiven();
        portfolioRepository.flush();

        // when
        RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .when().get(String.format("api/portfolios/%s/sector-ratio", portfolio.getId()))
                .then().log().all()
                .statusCode(SC_OK)
                .extract()
                .as(new TypeRef<>(){});

        // then
        assertThat(portfolioRepository.findById(portfolio.getId()).get().getHits()).isEqualTo(1);
    }

    @Test
    void 동시에_포트폴리오를_조회하면_정상적으로_조회수가_늘어난다() throws InterruptedException {
        // given
        Portfolio portfolio = stockAndDividendAndPortfolioGiven();
        portfolioRepository.flush();
        int threadCount = 100;
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(new ReadPortfolioTask(portfolio.getId(), latch));
            thread.start();
        }
        latch.await();

        // then
        assertThat(portfolioRepository.findById(portfolio.getId()).get().getHits()).isEqualTo(100);
    }

    private Portfolio stockAndDividendAndPortfolioGiven() {
        Stock aapl = stockRepository.save(StockFixture.createStock(AAPL, Sector.TECHNOLOGY));
        Stock tsla = stockRepository.save(StockFixture.createStock(TSLA, Sector.CONSUMER_CYCLICAL));

        dividendRepository.save(DividendFixture.createDividend(
                aapl.getId(),
                2.5,
                parseDate(InstantProvider.getLastYear(), 1)));
        dividendRepository.save(DividendFixture.createDividend(
                aapl.getId(),
                2.5,
                parseDate(InstantProvider.getLastYear(), 6)));
        dividendRepository.save(DividendFixture.createDividend(
                tsla.getId(),
                3.0,
                parseDate(InstantProvider.getLastYear(), 6)));

        return portfolioRepository.save(PortfolioFixture.createPortfolio(
                        LocalDate.now().plusMonths(1).atStartOfDay().toInstant(ZoneOffset.UTC),
                        List.of(new PortfolioStock(aapl.getId(), 2), new PortfolioStock(tsla.getId(), 1))
                )
        );
    }

    private Instant parseDate(int year, int month) {
        LocalDate date = LocalDate.of(year, month, 1);
        ZonedDateTime zonedDateTime = date.atStartOfDay(ZoneId.of("UTC"));
        return zonedDateTime.toInstant();
    }

    static class ReadPortfolioTask implements Runnable {

        private final UUID id;
        private final CountDownLatch latch;

        public ReadPortfolioTask(UUID id, CountDownLatch latch) {
            this.id = id;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                RestAssured
                        .given()
                        .log().all()
                        .contentType(ContentType.JSON)
                        .when().get(String.format("api/portfolios/%s/sector-ratio", id))
                        .then().log().all()
                        .statusCode(SC_OK)
                        .extract()
                        .as(new TypeRef<>(){});
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }
    }
}