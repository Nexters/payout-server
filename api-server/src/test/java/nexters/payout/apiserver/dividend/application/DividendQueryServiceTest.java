package nexters.payout.apiserver.dividend.application;

import nexters.payout.apiserver.dividend.application.dto.request.DividendRequest;
import nexters.payout.apiserver.dividend.application.dto.request.TickerShare;
import nexters.payout.apiserver.dividend.application.dto.response.MonthlyDividendResponse;
import nexters.payout.apiserver.dividend.application.dto.response.YearlyDividendResponse;
import nexters.payout.apiserver.dividend.common.GivenFixtureTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static nexters.payout.domain.StockFixture.*;
import static nexters.payout.domain.stock.domain.Sector.*;

@ExtendWith(MockitoExtension.class)
class DividendQueryServiceTest extends GivenFixtureTest {

    @InjectMocks
    private DividendQueryService dividendQueryService;

    @Test
    void 사용자의_월간_배당금_정보를_가져온다() {
        // given
        givenStockAndDividendForMonthly(AAPL, TECHNOLOGY, 2.5, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        givenStockAndDividendForMonthly(TSLA, UTILITIES, 4.2, 1, 4, 7, 10);
        givenStockAndDividendForMonthly(SBUX, CONSUMER_CYCLICAL, 5.0, 6, 12);
        double expected = 86.8;

        // when
        List<MonthlyDividendResponse> actual = dividendQueryService.getMonthlyDividends(request());

        // then
        assertAll(
                () -> assertThat(actual.size()).isEqualTo(12),
                () -> assertThat(actual
                        .stream()
                        .mapToDouble(MonthlyDividendResponse::totalDividend)
                        .sum()).isEqualTo(expected),
                () -> assertThat(actual.get(11).dividends().get(0).totalDividend()).isEqualTo(5.0)
        );
    }

    @Test
    void 사용자의_연간_배당금_정보를_가져온다() {
        // given
        givenStockAndDividendForYearly(AAPL, TECHNOLOGY, 2.5, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        givenStockAndDividendForYearly(TSLA, UTILITIES, 4.2, 1, 4, 7, 10);
        givenStockAndDividendForYearly(SBUX, CONSUMER_CYCLICAL, 5.0, 6, 12);
        double totalDividendExpected = 86.8;
        double aaplDividendExpected = 60.0;

        // when
        YearlyDividendResponse actual = dividendQueryService.getYearlyDividends(request());

        // then
        assertAll(
                () -> assertThat(actual.totalDividend()).isEqualTo(totalDividendExpected),
                () -> assertThat(actual.dividends()
                        .stream()
                        .filter(dividend -> dividend.ticker().equals(AAPL))
                        .findFirst().get()
                        .totalDividend())
                        .isEqualTo(aaplDividendExpected)
        );
    }

    private DividendRequest request() {
        return new DividendRequest(List.of(
                new TickerShare(AAPL, 2),
                new TickerShare(TSLA, 1),
                new TickerShare(SBUX, 1)));
    }
}