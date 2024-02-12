package nexters.payout.domain.stock.service;

import nexters.payout.domain.StockFixture;
import nexters.payout.domain.stock.Sector;
import nexters.payout.domain.stock.Stock;
import nexters.payout.domain.stock.service.SectorAnalysisService.SectorInfo;
import nexters.payout.domain.stock.service.SectorAnalysisService.StockShare;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectorAnalysisServiceTest {

    @Test
    void 하나의_티커가_존재하는_경우_섹터비율_검증() {
        // given
        Stock stock = StockFixture.createStock(StockFixture.AAPL, Sector.TECHNOLOGY, 3.0);
        List<StockShare> stockShares = List.of(new StockShare(stock, null, 1));
        SectorAnalysisService sectorAnalysisService = new SectorAnalysisService();

        // when
        Map<Sector, SectorInfo> actual = sectorAnalysisService.calculateSectorRatios(stockShares);

        // then
        assertAll(
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(actual.get(Sector.TECHNOLOGY)).isEqualTo(new SectorInfo(1.0, List.of(new StockShare(stock, null, 1))))
        );
    }

    @Test
    void 서로_다른_섹터와_개수와_현재가를_가진_2개의_티커가_존재하는_경우_섹터비율_검증() {
        // given
        Stock appl = StockFixture.createStock(StockFixture.AAPL, Sector.TECHNOLOGY, 4.0);
        Stock tsla = StockFixture.createStock(StockFixture.TSLA, Sector.CONSUMER_CYCLICAL, 1.0);
        List<StockShare> stockShares = List.of(new StockShare(appl, null, 2), new StockShare(tsla, null, 1));
        SectorAnalysisService sectorAnalysisService = new SectorAnalysisService();

        // when
        Map<Sector, SectorInfo> actual = sectorAnalysisService.calculateSectorRatios(stockShares);

        // then
        SectorInfo actualFinancialSectorInfo = actual.get(Sector.TECHNOLOGY);
        SectorInfo actualTechnologySectorInfo = actual.get(Sector.CONSUMER_CYCLICAL);

        assertAll(
                () -> assertThat(actual).hasSize(2),
                () -> assertThat(actualFinancialSectorInfo.ratio()).isCloseTo(0.8889, within(0.001)),
                () -> assertThat(actualFinancialSectorInfo.stockShares()).isEqualTo(List.of(new StockShare(appl, null, 2))),
                () -> assertThat(actualTechnologySectorInfo.ratio()).isCloseTo(0.1111, within(0.001)),
                () -> assertThat(actualTechnologySectorInfo.stockShares()).isEqualTo(List.of(new StockShare(tsla, null, 1)))
        );
    }

        assertAll(
                () -> assertThat(actual).hasSize(2),
                () -> assertThat(actualFinancialSectorInfo.ratio()).isCloseTo(0.8889, within(0.001)),
                () -> assertThat(actualFinancialSectorInfo.stockShares()).isEqualTo(List.of(new StockShare(appl, 2))),
                () -> assertThat(actualTechnologySectorInfo.ratio()).isCloseTo(0.1111, within(0.001)),
                () -> assertThat(actualTechnologySectorInfo.stockShares()).isEqualTo(List.of(new StockShare(tsla, null, 1)))
        );
    }
}