package nexters.payout.domain.stock.service;

import nexters.payout.domain.DividendFixture;
import nexters.payout.domain.StockFixture;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.stock.domain.Sector;
import nexters.payout.domain.stock.domain.Stock;
import nexters.payout.domain.stock.domain.service.StockDividendAnalysisService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;

class StockDividendAnalysisServiceTest {

    StockDividendAnalysisService stockDividendAnalysisService = new StockDividendAnalysisService();

    @Test
    void 작년_배당_월_리스트를_정상적으로_반환한다() {
        // given
        Stock stock = StockFixture.createStock(StockFixture.AAPL, Sector.TECHNOLOGY);
        int lastYear = LocalDate.now(UTC).getYear() - 1;
        Instant janPaymentDate = LocalDate.of(lastYear, 1, 3).atStartOfDay().toInstant(UTC);
        Instant aprPaymentDate = LocalDate.of(lastYear, 4, 3).atStartOfDay().toInstant(UTC);
        Instant julPaymentDate = LocalDate.of(lastYear, 7, 3).atStartOfDay().toInstant(UTC);
        Instant fakePaymentDate = LocalDate.of(LocalDate.now().getYear(), 8, 3).atStartOfDay().toInstant(UTC);

        Dividend janDividend = DividendFixture.createDividendWithExDividendDate(stock.getId(), janPaymentDate);
        Dividend aprDividend = DividendFixture.createDividendWithExDividendDate(stock.getId(), aprPaymentDate);
        Dividend julDividend = DividendFixture.createDividendWithExDividendDate(stock.getId(), julPaymentDate);
        Dividend fakeDividend = DividendFixture.createDividendWithExDividendDate(stock.getId(), fakePaymentDate);

        // when
        List<Month> actual = stockDividendAnalysisService.calculateDividendMonths(stock, List.of(janDividend, aprDividend, julDividend, fakeDividend));

        // then
        assertThat(actual).isEqualTo(List.of(Month.JANUARY, Month.APRIL, Month.JULY));
    }

    @Test
    void 작년_배당_기록이_없는_경우_빈_리스트를_반환한다() {
        // given
        Stock stock = StockFixture.createStock(StockFixture.AAPL, Sector.TECHNOLOGY);
        Instant fakePaymentDate = LocalDate.of(LocalDate.now().getYear(), 8, 3).atStartOfDay().toInstant(UTC);

        Dividend fakeDividend = DividendFixture.createDividendWithExDividendDate(stock.getId(), fakePaymentDate);

        // when
        List<Month> actual = stockDividendAnalysisService.calculateDividendMonths(stock, List.of(fakeDividend));

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void 배당_기록이_없는_경우_빈_리스트를_반환한다() {
        // given
        Stock stock = StockFixture.createStock(StockFixture.AAPL, Sector.TECHNOLOGY);

        // when
        List<Month> actual = stockDividendAnalysisService.calculateDividendMonths(stock, List.of());

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void 공시된_현재_배당금_지급일이_없는_경우_과거데이터를_기반으로_가까운_지급일을_계산한다() {
        // given
        LocalDate now = LocalDate.now();

        Dividend pastDividend = DividendFixture.createDividendWithExDividendDate(
                UUID.randomUUID(),
                LocalDate.of(now.getYear(), now.getMonth().minus(1), now.getDayOfMonth())
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()
        );

        Dividend earlistDividend = DividendFixture.createDividendWithExDividendDate(
                UUID.randomUUID(),
                LocalDate.of(now.getYear(), now.getMonth(), now.getDayOfMonth() + 1)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
        List<Dividend> lastYearDividends = List.of(pastDividend, earlistDividend);

        // when
        Optional<Dividend> actual = stockDividendAnalysisService.findUpcomingDividend(lastYearDividends, Collections.emptyList());

        // then
        assertThat(actual.get()).isEqualTo(earlistDividend);
    }

    @Test
    void 공시된_현재_배당금_지급일이_존재하는_경우_실제_지급일을_반환한다() {
        // given
        LocalDate now = LocalDate.now();
        int plusDay = Math.max(now.getDayOfMonth(), now.plusDays(3).getDayOfMonth());

        Dividend lastYearDividend = DividendFixture.createDividend(
                UUID.randomUUID(),
                1.0,
                LocalDate.now().minusDays(10)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()
        );

        Dividend thisYearDividend = DividendFixture.createDividend(
                UUID.randomUUID(),
                1.0,
                LocalDate.now().plusDays(3)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()
        );

        List<Dividend> lastYearDividends = List.of(lastYearDividend);
        List<Dividend> thisYearDividends = List.of(thisYearDividend);

        // when
        Optional<Dividend> actual = stockDividendAnalysisService.findUpcomingDividend(lastYearDividends, thisYearDividends);

        // then
        assertThat(actual.get()).isEqualTo(thisYearDividend);
    }

    @Test
    void 배당수익률을_구할수있다() {
        // given
        Stock aapl = StockFixture.createStock(StockFixture.AAPL, Sector.TECHNOLOGY, 40.0);
        List<Dividend> dividends = List.of(DividendFixture.createDividendWithDividend(UUID.randomUUID(), 10.0),
                DividendFixture.createDividendWithDividend(UUID.randomUUID(), 20.0)
        );
        Double expected = 30.0 / 40.0;

        // when
        Double actual = stockDividendAnalysisService.calculateDividendYield(aapl, dividends);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 배당금_리스트로부터_평균_배당금을_구할수있다() {
        // given
        List<Dividend> dividends = List.of(DividendFixture.createDividendWithDividend(UUID.randomUUID(), 10.0),
                DividendFixture.createDividendWithDividend(UUID.randomUUID(), 20.0)
        );

        // when
        Double actual = stockDividendAnalysisService.calculateAverageDividend(dividends);

        // then
        assertThat(actual).isEqualTo(15.0);
    }
}