package nexters.payout.apiserver.portfolio.common;

import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.DividendFixture;
import nexters.payout.domain.StockFixture;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.dividend.domain.repository.DividendRepository;
import nexters.payout.domain.stock.domain.Sector;
import nexters.payout.domain.stock.domain.Stock;
import nexters.payout.domain.stock.domain.repository.StockRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public abstract class GivenFixtureTest {

    private final Integer JANUARY = 1;
    private final Integer DECEMBER = 12;

    @Mock
    protected DividendRepository dividendRepository;

    @Mock
    protected StockRepository stockRepository;

    public Stock givenStockAndDividendForMonthly(String ticker, Sector sector, double dividend, int... cycle) {
        Stock stock = StockFixture.createStock(ticker, sector);
        given(stockRepository.findById(eq(stock.getId()))).willReturn(Optional.of(stock));

        for (int month = JANUARY; month <= DECEMBER; month++) {
            if (isContain(cycle, month)) {
                // 배당 주기에 해당하는 경우
                given(dividendRepository.findAllByIdAndYearAndMonth(
                        eq(stock.getId()),
                        eq(InstantProvider.getLastYear()),
                        eq(month)))
                        .willReturn(List.of(DividendFixture.createDividend(
                                stock.getId(),
                                dividend,
                                parseDate(InstantProvider.getLastYear(), month)
                        )));
            } else {
                // 배당 주기에 해당하지 않는 경우
                given(dividendRepository.findAllByIdAndYearAndMonth(
                        eq(stock.getId()),
                        eq(InstantProvider.getLastYear()),
                        eq(month)))
                        .willReturn(new ArrayList<>());
            }
        }

        return stock;
    }

    public Stock givenStockAndDividendForYearly(String ticker, Sector sector, double dividend, int... cycle) {
        Stock stock = StockFixture.createStock(ticker, sector);
        given(stockRepository.findById(eq(stock.getId()))).willReturn(Optional.of(stock));

        List<Dividend> dividends = new ArrayList<>();
        for (int month : cycle) {
            dividends.add(DividendFixture.createDividend(
                    stock.getId(),
                    dividend,
                    parseDate(InstantProvider.getLastYear(), month)));
        }

        given(dividendRepository.findAllByIdAndYear(
                eq(stock.getId()),
                eq(InstantProvider.getLastYear())))
                .willReturn(dividends);

        return stock;
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
