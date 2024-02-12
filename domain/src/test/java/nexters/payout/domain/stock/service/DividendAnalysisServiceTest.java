package nexters.payout.domain.stock.service;

import nexters.payout.domain.DividendFixture;
import nexters.payout.domain.StockFixture;
import nexters.payout.domain.dividend.Dividend;
import nexters.payout.domain.stock.Sector;
import nexters.payout.domain.stock.Stock;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;

class DividendAnalysisServiceTest {

    @Test
    void 작년_배당_월_리스트를_정상적으로_반환한다() {
        // given
        Stock stock = StockFixture.createStock(StockFixture.AAPL, Sector.TECHNOLOGY);
        int lastYear = LocalDate.now(UTC).getYear() - 1;
        Instant janPaymentDate = LocalDate.of(lastYear, 1, 3).atStartOfDay().toInstant(UTC);
        Instant aprPaymentDate = LocalDate.of(lastYear, 4, 3).atStartOfDay().toInstant(UTC);
        Instant julPaymentDate = LocalDate.of(lastYear, 7, 3).atStartOfDay().toInstant(UTC);
        Instant fakePaymentDate = LocalDate.of(LocalDate.now().getYear(), 8, 3).atStartOfDay().toInstant(UTC);

        Dividend janDividend = DividendFixture.createDividend(stock.getId(), janPaymentDate);
        Dividend aprDividend = DividendFixture.createDividend(stock.getId(), aprPaymentDate);
        Dividend julDividend = DividendFixture.createDividend(stock.getId(), julPaymentDate);
        Dividend fakeDividend = DividendFixture.createDividend(stock.getId(), fakePaymentDate);

        // when
        DividendAnalysisService service = new DividendAnalysisService();
        List<Month> actual = service.calculateDividendMonths(stock, List.of(janDividend, aprDividend, julDividend, fakeDividend));

        // then
        assertThat(actual).isEqualTo(List.of(Month.JANUARY, Month.APRIL, Month.JULY));
    }

    @Test
    void 작년_배당_기록이_없는_경우_빈_리스트를_반환한다() {
        // given
        Stock stock = StockFixture.createStock(StockFixture.AAPL, Sector.TECHNOLOGY);
        Instant fakePaymentDate = LocalDate.of(LocalDate.now().getYear(), 8, 3).atStartOfDay().toInstant(UTC);

        Dividend fakeDividend = DividendFixture.createDividend(stock.getId(), fakePaymentDate);

        // when
        DividendAnalysisService service = new DividendAnalysisService();
        List<Month> actual = service.calculateDividendMonths(stock, List.of(fakeDividend));

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void 배당_기록이_없는_경우_빈_리스트를_반환한다() {
        // given
        Stock stock = StockFixture.createStock(StockFixture.AAPL, Sector.TECHNOLOGY);

        // when
        DividendAnalysisService service = new DividendAnalysisService();
        List<Month> actual = service.calculateDividendMonths(stock, List.of());

        // then
        assertThat(actual).isEmpty();
    }
}