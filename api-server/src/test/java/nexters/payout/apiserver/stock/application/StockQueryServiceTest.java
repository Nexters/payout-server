package nexters.payout.apiserver.stock.application;

import nexters.payout.apiserver.dividend.application.StockDividendQueryServiceImpl;
import nexters.payout.apiserver.stock.application.dto.request.SectorRatioRequest;
import nexters.payout.apiserver.stock.application.dto.request.TickerShare;
import nexters.payout.apiserver.stock.application.dto.response.*;
import nexters.payout.core.time.InstantProvider;
import nexters.payout.apiserver.stock.application.dto.response.SingleUpcomingDividendResponse;
import nexters.payout.apiserver.stock.application.dto.response.SectorRatioResponse;
import nexters.payout.apiserver.stock.application.dto.response.StockDetailResponse;
import nexters.payout.apiserver.stock.application.dto.response.StockResponse;
import nexters.payout.domain.DividendFixture;
import nexters.payout.domain.StockFixture;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.dividend.domain.repository.DividendRepository;
import nexters.payout.domain.stock.infra.dto.StockDividendDto;
import nexters.payout.domain.stock.domain.Sector;
import nexters.payout.domain.stock.domain.Stock;
import nexters.payout.domain.stock.domain.repository.StockRepository;
import nexters.payout.domain.stock.domain.service.StockDividendAnalysisService;
import nexters.payout.domain.stock.domain.service.SectorAnalysisService;
import nexters.payout.domain.stock.infra.dto.StockDividendYieldDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static nexters.payout.domain.StockFixture.*;
import static nexters.payout.domain.stock.domain.Sector.TECHNOLOGY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StockQueryServiceTest {

    private StockQueryService stockQueryService;
    @Mock
    private StockRepository stockRepository;
    @Mock
    private DividendRepository dividendRepository;
    @Spy
    private SectorAnalysisService sectorAnalysisService;
    @Spy
    private StockDividendAnalysisService stockDividendAnalysisService;

    @BeforeEach
    void setUp() {
        StockDividendQueryServiceImpl stockDividendQuery = new StockDividendQueryServiceImpl(stockDividendAnalysisService, stockRepository, dividendRepository);
        stockQueryService = new StockQueryService(stockRepository, sectorAnalysisService, stockDividendQuery);
    }

    @Test
    void 검색된_종목_정보를_정상적으로_반환한다() {
        // given
        given(stockRepository.findStocksByTickerOrNameWithPriority(any(), any(), any())).willReturn(List.of(StockFixture.createStock(AAPL, Sector.TECHNOLOGY)));

        // when
        List<StockResponse> actual = stockQueryService.searchStock("A", 1, 2);

        // then
        assertAll(
                () -> assertThat(actual.get(0).ticker()).isEqualTo(AAPL),
                () -> assertThat(actual.get(0).sectorName()).isEqualTo(Sector.TECHNOLOGY.getName()),
                () -> assertThat(actual.get(0).logoUrl()).isEqualTo("")
        );
    }

    @Test
    void 종목_상세_정보를_정상적으로_반환한다() {
        // given
        int lastYear = LocalDate.now(UTC).getYear() - 1;
        int expectedMonth = 3;
        Instant exDividendDate = LocalDate.of(lastYear, expectedMonth, 1).atStartOfDay().toInstant(UTC);
        Double expectedPrice = 2.0;
        Double expectedDividend = 0.5;
        Stock aapl = StockFixture.createStock(AAPL, Sector.TECHNOLOGY, 2.0);
        Dividend dividend = DividendFixture.createDividend(aapl.getId(), 0.5, exDividendDate);

        given(stockRepository.findByTicker(any())).willReturn(Optional.of(aapl));
        given(dividendRepository.findAllByStockId(any())).willReturn(List.of(dividend));

        // when
        StockDetailResponse actual = stockQueryService.getStockByTicker(aapl.getTicker());

        // then
        assertAll(
                () -> assertThat(actual.ticker()).isEqualTo(aapl.getTicker()),
                () -> assertThat(actual.industry()).isEqualTo(aapl.getIndustry()),
                () -> assertThat(actual.dividendYield()).isEqualTo(expectedDividend / expectedPrice),
                () -> assertThat(actual.dividendMonths()).isEqualTo(List.of(Month.of(expectedMonth)))
        );
    }

    @Test
    void 종목_상세_정보의_배당날짜를_올해기준으로_반환한다() {
        // given
        LocalDate expectedDate = LocalDate.now().minusYears(1).plusDays(1);
        int lastYear = LocalDate.now().getYear() - 1;
        Instant exDividendDate = LocalDate.now().minusYears(1).plusDays(1).atStartOfDay().toInstant(UTC);
        Stock appl = StockFixture.createStock(AAPL, Sector.TECHNOLOGY, 2.0);
        Dividend dividend = DividendFixture.createDividendWithPaymentDate(appl.getId(), 0.5, exDividendDate);

        given(stockRepository.findByTicker(any())).willReturn(Optional.of(appl));
        given(dividendRepository.findAllByStockId(any())).willReturn(List.of(dividend));

        // when
        StockDetailResponse actual = stockQueryService.getStockByTicker(appl.getTicker());

        // then
        assertThat(actual.earliestPaymentDate()).isEqualTo(LocalDate.of(lastYear + 1, expectedDate.getMonth(), expectedDate.getDayOfMonth()));
    }

    @Test
    void 섹터_정보를_정상적으로_반환한다() {
        // given
        SectorRatioRequest request = new SectorRatioRequest(List.of(new TickerShare(AAPL, 2), new TickerShare(TSLA, 3)));
        Stock appl = StockFixture.createStock(AAPL, Sector.TECHNOLOGY, 4.0);
        Stock tsla = StockFixture.createStock(TSLA, Sector.CONSUMER_CYCLICAL, 2.2);
        List<Stock> stocks = List.of(appl, tsla);

        given(stockRepository.findAllByTickerIn(any())).willReturn(stocks);

        List<SectorRatioResponse> expected = List.of(
                new SectorRatioResponse(
                        Sector.TECHNOLOGY.getName(),
                        Sector.TECHNOLOGY.name(),
                        0.547945205479452,
                        List.of(new StockShareResponse(
                                StockResponse.from(appl),
                                2
                        ))
                ),
                new SectorRatioResponse(
                        Sector.CONSUMER_CYCLICAL.getName(),
                        Sector.CONSUMER_CYCLICAL.name(),
                        0.4520547945205479,
                        List.of(new StockShareResponse(
                                StockResponse.from(tsla),
                                3
                        ))
                )
        );

        // when
        List<SectorRatioResponse> actual = stockQueryService.analyzeSectorRatio(request);

        // then
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void 배당락일이_다가오는_주식_리스트를_가져온다() {
        // given
        Stock stock = StockFixture.createStock(AAPL, TECHNOLOGY);
        Dividend expected = DividendFixture.createDividendWithExDividendDate(stock.getId(), LocalDateTime.now().plusDays(1).toInstant(UTC));
        given(stockRepository.findUpcomingDividendStock(TECHNOLOGY, 1, 10))
                .willReturn(List.of(new StockDividendDto(stock, expected)));

        // when
        List<SingleUpcomingDividendResponse> actual = stockQueryService.getUpcomingDividendStocks(TECHNOLOGY, 1, 10).dividends();

        // then
        assertAll(
                () -> assertThat(actual.size()).isEqualTo(1),
                () -> assertThat(actual.get(0).exDividendDate()).isEqualTo(expected.getExDividendDate()),
                () -> assertThat(actual.get(0).ticker()).isEqualTo(stock.getTicker())
        );
    }

    @Test
    void 배당_수익률이_큰_순서대로_주식_리스트를_가져온다() {
        // given
        Stock expected = StockFixture.createStock(AAPL, TECHNOLOGY, 2.0);
        Stock tsla = StockFixture.createStock(TSLA, TECHNOLOGY, 3.0);
        given(stockRepository.findBiggestDividendYieldStock(InstantProvider.getLastYear(), TECHNOLOGY, 1, 10))
                .willReturn(List.of(
                        new StockDividendYieldDto(expected, 5.0),
                        new StockDividendYieldDto(tsla, 4.0))
                );
        Double expectedAaplDividendYield = 5.0;

        // when
        List<SingleStockDividendYieldResponse> actual = stockQueryService.getBiggestDividendStocks(TECHNOLOGY, 1, 10).dividends();


        // then
        assertAll(
                () -> assertThat(actual.size()).isEqualTo(2),
                () -> assertThat(actual.get(0).stockId()).isEqualTo(expected.getId()),
                () -> assertThat(actual.get(0).dividendYield()).isEqualTo(expectedAaplDividendYield)
        );
    }
}