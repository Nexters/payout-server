package nexters.payout.apiserver.stock.application;

import nexters.payout.apiserver.stock.application.dto.request.SectorRatioRequest;
import nexters.payout.apiserver.stock.application.dto.request.TickerShare;
import nexters.payout.apiserver.stock.application.dto.response.SectorRatioResponse;
import nexters.payout.apiserver.stock.application.dto.response.StockDetailResponse;
import nexters.payout.apiserver.stock.application.dto.response.StockResponse;
import nexters.payout.domain.DividendFixture;
import nexters.payout.domain.StockFixture;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.dividend.domain.repository.DividendRepository;
import nexters.payout.domain.stock.domain.Sector;
import nexters.payout.domain.stock.domain.Stock;
import nexters.payout.domain.stock.domain.repository.StockRepository;
import nexters.payout.domain.stock.infra.StockRepositoryCustom;
import nexters.payout.domain.stock.domain.service.DividendAnalysisService;
import nexters.payout.domain.stock.domain.service.SectorAnalysisService;
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
class StockQueryServiceTest {

    @InjectMocks
    private StockQueryService stockQueryService;

    @Mock
    private StockRepository stockRepository;
    @Mock
    private DividendRepository dividendRepository;
    @Spy
    private SectorAnalysisService sectorAnalysisService;
    @Spy
    private DividendAnalysisService dividendAnalysisService;

    @Test
    void 검색된_종목_정보를_정상적으로_반환한다() {
        // given
        given(stockRepository.findStocksByTickerOrNameWithPriority(any(), any(), any())).willReturn(List.of(StockFixture.createStock(AAPL, Sector.TECHNOLOGY)));

        // when
        List<StockResponse> actual = stockQueryService.searchStock("A", 1 , 2);

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
        Double expectedPrice = 2.0;
        Double expectedDividend = 0.5;
        Stock aapl = StockFixture.createStock(AAPL, Sector.TECHNOLOGY, 2.0);
        int lastYear = LocalDate.now(UTC).getYear() - 1;
        Instant janPaymentDate = LocalDate.of(lastYear, 1, 3).atStartOfDay().toInstant(UTC);
        Dividend dividend = DividendFixture.createDividend(aapl.getId(), 0.5, janPaymentDate);

        given(stockRepository.findByTicker(any())).willReturn(Optional.of(aapl));
        given(dividendRepository.findAllByStockId(any())).willReturn(List.of(dividend));

        // when
        StockDetailResponse actual = stockQueryService.getStockByTicker(aapl.getTicker());

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
        StockDetailResponse actual = stockQueryService.getStockByTicker(appl.getTicker());

        // then
        assertThat(actual.earliestPaymentDate()).isEqualTo(LocalDate.of(lastYear + 1, 1, 3));
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
                                appl.getLogoUrl()
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
                                        appl.getLogoUrl()
                                )
                        )
                )
        );

        // when
        List<SectorRatioResponse> actual = stockQueryService.analyzeSectorRatio(request);

        // then
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }
}