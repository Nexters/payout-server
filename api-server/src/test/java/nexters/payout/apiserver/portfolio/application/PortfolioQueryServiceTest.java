package nexters.payout.apiserver.portfolio.application;

import nexters.payout.apiserver.portfolio.application.dto.request.TickerShare;
import nexters.payout.apiserver.portfolio.application.dto.request.PortfolioRequest;
import nexters.payout.apiserver.portfolio.application.dto.response.MonthlyDividendResponse;
import nexters.payout.apiserver.portfolio.application.dto.response.PortfolioResponse;
import nexters.payout.apiserver.portfolio.application.dto.response.YearlyDividendResponse;
import nexters.payout.apiserver.portfolio.common.GivenFixtureTest;
import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.PortfolioFixture;
import nexters.payout.domain.StockFixture;
import nexters.payout.domain.portfolio.domain.Portfolio;
import nexters.payout.domain.portfolio.domain.PortfolioStock;
import nexters.payout.domain.portfolio.domain.repository.PortfolioRepository;
import nexters.payout.domain.stock.domain.Sector;
import nexters.payout.domain.stock.domain.Stock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static nexters.payout.domain.stock.domain.Sector.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static nexters.payout.domain.StockFixture.*;

@ExtendWith(MockitoExtension.class)
class PortfolioQueryServiceTest extends GivenFixtureTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private PortfolioQueryService portfolioQueryService;

    @Test
    void 포트폴리오를_생성한다() {
        // given
        Stock appl = StockFixture.createStock(AAPL, Sector.TECHNOLOGY, 4.0);
        Stock tsla = StockFixture.createStock(TSLA, Sector.CONSUMER_CYCLICAL, 2.2);
        given(stockRepository.findByTicker(eq(AAPL))).willReturn(Optional.of(appl));
        given(stockRepository.findByTicker(eq(TSLA))).willReturn(Optional.of(tsla));

        given(portfolioRepository.save(any())).willReturn(PortfolioFixture.createPortfolio(
                        UUID.fromString("67221662-c2f7-4f35-9447-6a65ca88d5ea"),
                        InstantProvider.getExpireAt(),
                        List.of(
                                new PortfolioStock(UUID.randomUUID(), 2),
                                new PortfolioStock(UUID.randomUUID(), 1)
                        )
                )
        );
        String expected = "67221662-c2f7-4f35-9447-6a65ca88d5ea";

        // when
        PortfolioResponse actual = portfolioQueryService.createPortfolio(request());

        // then
        assertThat(actual.id()).isEqualTo(UUID.fromString(expected));
    }

    @Test
    void 사용자의_월간_배당금_정보를_가져온다() {
        // given
        UUID id = UUID.fromString("bf5ffb6d-ae70-4171-8c86-b27c8ab2efbb");
        givenPortfolioForMonthlyDividend(id);
        double expected = 86.8;

        // when
        List<MonthlyDividendResponse> actual = portfolioQueryService.getMonthlyDividends(id);

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
        UUID id = UUID.fromString("bf5ffb6d-ae70-4171-8c86-b27c8ab2efbb");
        givenPortfolioForYearlyDividend(id);
        double totalDividendExpected = 86.8;
        double aaplDividendExpected = 60.0;

        // when
        YearlyDividendResponse actual = portfolioQueryService.getYearlyDividends(id);

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

    private void givenPortfolioForMonthlyDividend(UUID id) {
        Stock aapl = givenStockAndDividendForMonthly(AAPL, TECHNOLOGY, 2.5, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        Stock tsla = givenStockAndDividendForMonthly(TSLA, UTILITIES, 4.2, 1, 4, 7, 10);
        Stock sbux = givenStockAndDividendForMonthly(SBUX, CONSUMER_CYCLICAL, 5.0, 6, 12);

        List<PortfolioStock> portfolioStocks = new ArrayList<>();

        portfolioStocks.add(new PortfolioStock(aapl.getId(), 2));
        portfolioStocks.add(new PortfolioStock(tsla.getId(), 1));
        portfolioStocks.add(new PortfolioStock(sbux.getId(), 1));

        Portfolio portfolio = PortfolioFixture.createPortfolio(
                id,
                LocalDate.now().plusMonths(1).atStartOfDay().toInstant(ZoneOffset.UTC),
                portfolioStocks
        );

        given(portfolioRepository.findById(eq(id))).willReturn(Optional.of(portfolio));
    }

    private void givenPortfolioForYearlyDividend(UUID id) {
        Stock aapl = givenStockAndDividendForYearly(AAPL, TECHNOLOGY, 2.5, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        Stock tsla = givenStockAndDividendForYearly(TSLA, UTILITIES, 4.2, 1, 4, 7, 10);
        Stock sbux = givenStockAndDividendForYearly(SBUX, CONSUMER_CYCLICAL, 5.0, 6, 12);

        List<PortfolioStock> portfolioStocks = new ArrayList<>();

        portfolioStocks.add(new PortfolioStock(aapl.getId(), 2));
        portfolioStocks.add(new PortfolioStock(tsla.getId(), 1));
        portfolioStocks.add(new PortfolioStock(sbux.getId(), 1));

        Portfolio portfolio = PortfolioFixture.createPortfolio(
                id,
                LocalDate.now().plusMonths(1).atStartOfDay().toInstant(ZoneOffset.UTC),
                portfolioStocks
        );

        given(portfolioRepository.findById(eq(id))).willReturn(Optional.of(portfolio));
    }

    private PortfolioRequest request() {
        return new PortfolioRequest(List.of(
                new TickerShare(AAPL, 2),
                new TickerShare(TSLA, 1))
        );
    }
}