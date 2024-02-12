package nexters.payout.apiserver.stock.application;

import nexters.payout.apiserver.stock.application.dto.request.SectorRatioRequest;
import nexters.payout.apiserver.stock.application.dto.request.TickerShare;
import nexters.payout.apiserver.stock.application.dto.response.SectorRatioResponse;
import nexters.payout.apiserver.stock.application.dto.response.StockDetailResponse;
import nexters.payout.apiserver.stock.application.dto.response.StockResponse;
import nexters.payout.domain.DividendFixture;
import nexters.payout.domain.StockFixture;
import nexters.payout.domain.dividend.Dividend;
import nexters.payout.domain.dividend.repository.DividendRepository;
import nexters.payout.domain.stock.Sector;
import nexters.payout.domain.stock.Stock;
import nexters.payout.domain.stock.repository.StockRepository;
import nexters.payout.domain.stock.service.DividendAnalysisService;
import nexters.payout.domain.stock.service.SectorAnalysisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static nexters.payout.domain.StockFixture.AAPL;
import static nexters.payout.domain.StockFixture.TSLA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockService stockService;

    @Mock
    private StockRepository stockRepository;
    @Mock
    private DividendRepository dividendRepository;
    @Spy
    private SectorAnalysisService sectorAnalysisService;
    @Spy
    private DividendAnalysisService dividendAnalysisService;

    @Test
    void 종목_상세_정보를_정싱적으로_반환한다() {
        // given
        Double expectedPrice = 2.0;
        Double expectedDividend = 0.5;
        Stock aapl = StockFixture.createStock(AAPL, Sector.TECHNOLOGY, 2.0);
        int lastYear = LocalDate.now(UTC).getYear() - 1;
        Instant janPaymentDate = LocalDate.of(lastYear, 1, 3).atStartOfDay().toInstant(UTC);
        Dividend dividend = DividendFixture.createDividend(aapl.getId(), 0.5, janPaymentDate);

        given(stockRepository.findByTicker(any())).willReturn(Optional.of(aapl));
        given(dividendRepository.findAllByStockId(any())).willReturn(List.of(dividend));

        // when
        StockDetailResponse actual = stockService.getStockByTicker(aapl.getTicker());

        // then
        assertAll(
                () -> assertThat(actual.ticker()).isEqualTo(aapl.getTicker()),
                () -> assertThat(actual.industry()).isEqualTo(aapl.getIndustry()),
                () -> assertThat(actual.dividendYield()).isEqualTo(expectedDividend / expectedPrice),
                () -> assertThat(actual.dividendMonths()).isEqualTo(List.of(Month.JANUARY))
        );
    }

    @Test
    void 종목_상세_정보의_배당날짜를_올해기준으로_반환한다() {
        // given
        Stock appl = StockFixture.createStock(AAPL, Sector.TECHNOLOGY, 2.0);
        int lastYear = LocalDate.now(UTC).getYear() - 1;
        Instant janPaymentDate = LocalDate.of(lastYear, 1, 3).atStartOfDay().toInstant(UTC);
        Dividend dividend = DividendFixture.createDividend(appl.getId(), 0.5, janPaymentDate);

        given(stockRepository.findByTicker(any())).willReturn(Optional.of(appl));
        given(dividendRepository.findAllByStockId(any())).willReturn(List.of(dividend));

        // when
        StockDetailResponse actual = stockService.getStockByTicker(appl.getTicker());

        // then
        assertThat(actual.earliestPaymentDate()).isEqualTo(LocalDate.of(lastYear + 1, 1, 3));
    }

    @Test
    void 섹터_정보를_정상적으로_반환한다() {
        // given
        SectorRatioRequest request = new SectorRatioRequest(List.of(new TickerShare(AAPL, 2), new TickerShare(TSLA, 3)));
        Stock appl = StockFixture.createStock(AAPL, Sector.TECHNOLOGY, 4.0);
        Stock tsla = StockFixture.createStock(TSLA, Sector.CONSUMER_CYCLICAL, 2.2);
        Dividend aaplDiv = DividendFixture.createDividend(appl.getId(), 11.0);
        Dividend tslaDiv = DividendFixture.createDividend(tsla.getId(), 5.0);
        List<Stock> stocks = List.of(appl, tsla);
        List<Dividend> dividends = List.of(aaplDiv, tslaDiv);

        given(stockRepository.findAllByTickerIn(any())).willReturn(stocks);
        given(dividendRepository.findAllByStockIdIn(any())).willReturn(dividends);

        List<SectorRatioResponse> expected = List.of(
                new SectorRatioResponse(
                        Sector.TECHNOLOGY.getName(),
                        0.547945205479452,
                        List.of(new StockResponse(
                                appl.getId(),
                                appl.getTicker(),
                                appl.getName(),
                                appl.getSector().getName(),
                                appl.getExchange(),
                                appl.getIndustry(),
                                appl.getPrice(),
                                appl.getVolume(),
                                aaplDiv.getDividend()
                        ))
                ),
                new SectorRatioResponse(
                        Sector.CONSUMER_CYCLICAL.getName(),
                        0.4520547945205479,
                        List.of(new StockResponse(
                                tsla.getId(),
                                tsla.getTicker(),
                                tsla.getName(),
                                tsla.getSector().getName(),
                                tsla.getExchange(),
                                tsla.getIndustry(),
                                tsla.getPrice(),
                                tsla.getVolume(),
                                tslaDiv.getDividend())
                        )
                )
        );

        // when
        List<SectorRatioResponse> actual = stockService.analyzeSectorRatio(request);

        // then
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }
}