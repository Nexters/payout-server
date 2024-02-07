package nexters.payout.apiserver.stock.application;

import nexters.payout.apiserver.stock.application.dto.response.SectorRatioResponse;
import nexters.payout.domain.StockFixture;
import nexters.payout.domain.stock.Sector;
import nexters.payout.domain.stock.Stock;
import nexters.payout.domain.stock.repository.StockRepository;
import nexters.payout.domain.stock.service.SectorAnalyzer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockService stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private SectorAnalyzer sectorAnalyzer;

    @Test
    void 포트폴리오에_존재하는_섹터개수만큼_비율을_정상적으로_반환한다() {
        // given
        List<String> tickers = List.of(StockFixture.APPL, StockFixture.TSLA);
        List<Stock> stocks = List.of(
                StockFixture.createStock(StockFixture.APPL, Sector.TECHNOLOGY),
                StockFixture.createStock(StockFixture.TSLA, Sector.CONSUMER_CYCLICAL));

        given(stockRepository.findAllByTickerIn(any())).willReturn(stocks);
        given(sectorAnalyzer.calculateSectorRatios(any())).willReturn(Map.of(Sector.TECHNOLOGY, 0.5, Sector.CONSUMER_CYCLICAL, 0.5));

        List<SectorRatioResponse> expected = List.of(
                new SectorRatioResponse(Sector.TECHNOLOGY.getName(), 0.5),
                new SectorRatioResponse(Sector.CONSUMER_CYCLICAL.getName(), 0.5));

        // when
        List<SectorRatioResponse> actual = stockService.findSectorRatios(tickers);

        // then
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }
}