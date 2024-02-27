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

        Dividend janDividend = DividendFixture.createDividendWithPaymentDate(stock.getId(), janPaymentDate);
        Dividend aprDividend = DividendFixture.createDividendWithPaymentDate(stock.getId(), aprPaymentDate);
        Dividend julDividend = DividendFixture.createDividendWithPaymentDate(stock.getId(), julPaymentDate);
        Dividend fakeDividend = DividendFixture.createDividendWithPaymentDate(stock.getId(), fakePaymentDate);

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

        Dividend fakeDividend = DividendFixture.createDividendWithPaymentDate(stock.getId(), fakePaymentDate);

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

        Dividend pastDividend = DividendFixture.createDividendWithPaymentDate(
                UUID.randomUUID(),
                LocalDate.of(now.getYear() - 1, 1, 10)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()
        );

        Dividend earlistDividend = DividendFixture.createDividendWithPaymentDate(
                UUID.randomUUID(),
                LocalDate.of(now.getYear() - 1, 3, 10)
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

        Dividend lastYearDividend = DividendFixture.createDividendWithExDividendDate(
                UUID.randomUUID(),
                1.0,
                LocalDate.now().plusDays(10)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()
        );

        Dividend thisYearDividend = DividendFixture.createDividendWithExDividendDate(
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
}