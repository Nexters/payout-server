package nexters.payout.apiserver.dividend.application;

import nexters.payout.apiserver.dividend.application.dto.request.DividendRequest;
import nexters.payout.apiserver.dividend.application.dto.request.TickerShare;
import nexters.payout.apiserver.dividend.application.dto.response.MonthlyDividendResponse;
import nexters.payout.apiserver.dividend.application.dto.response.YearlyDividendResponse;
import nexters.payout.apiserver.dividend.infra.eodhd.EodhdProperties;
import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.DividendFixture;
import nexters.payout.domain.StockFixture;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.dividend.domain.repository.DividendRepository;
import nexters.payout.domain.stock.domain.Sector;
import nexters.payout.domain.stock.domain.Stock;
import nexters.payout.domain.stock.domain.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static nexters.payout.domain.StockFixture.*;
import static nexters.payout.domain.stock.domain.Sector.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@SpringBootTest
class DividendQueryServiceTest {

    @Autowired
    private DividendQueryService dividendQueryService;

    @MockBean
    private DividendRepository dividendRepository;

    @MockBean
    private StockRepository stockRepository;

    @Autowired
    private EodhdProperties eodhdProperties;

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
                () -> assertThat(actual.stream()
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
                () -> assertThat(actual.dividends().stream()
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

    private void givenStockAndDividendForMonthly(String ticker, Sector sector, double dividend, int... cycle) {
        Stock stock = StockFixture.createStock(ticker, sector);
        given(stockRepository.findByTicker(eq(ticker))).willReturn(Optional.of(stock));

        for (int month = 1; month <= 12; month++) {
            if (isContain(cycle, month)) {
                // 배당 주기에 해당하는 경우
                given(dividendRepository.findAllByTickerAndYearAndMonth(
                        eq(ticker),
                        eq(InstantProvider.getLastYear()),
                        eq(month)))
                        .willReturn(List.of(DividendFixture.createDividendWithExDividendDate(
                                stock.getId(),
                                dividend,
                                parseDate(InstantProvider.getLastYear(), month)
                        )));
            } else {
                // 배당 주기에 해당하지 않는 경우
                given(dividendRepository.findAllByTickerAndYearAndMonth(
                        eq(ticker),
                        eq(InstantProvider.getLastYear()),
                        eq(month)))
                        .willReturn(new ArrayList<>());
            }
        }
    }

    private void givenStockAndDividendForYearly(String ticker, Sector sector, double dividend, int... cycle) {
        Stock stock = StockFixture.createStock(ticker, sector);
        given(stockRepository.findByTicker(eq(ticker))).willReturn(Optional.of(stock));

        List<Dividend> dividends = new ArrayList<>();
        for (int month : cycle) {
            dividends.add(DividendFixture.createDividendWithExDividendDate(
                    stock.getId(),
                    dividend,
                    parseDate(InstantProvider.getLastYear(), month)));
        }

        given(dividendRepository.findAllByTickerAndYear(
                eq(ticker),
                eq(InstantProvider.getLastYear())))
                .willReturn(dividends);
    }

    private boolean isContain(int[] cycle, int month) {
        return Arrays.stream(cycle).anyMatch(m -> m == month);
    }

    private Instant parseDate(int year, int month) {
        LocalDate date = LocalDate.of(year, month, 1);
        ZonedDateTime zonedDateTime = date.atStartOfDay(ZoneId.of("UTC"));
        return zonedDateTime.toInstant();
    }
}