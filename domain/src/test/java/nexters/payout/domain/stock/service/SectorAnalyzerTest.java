package nexters.payout.domain.stock.service;

import nexters.payout.domain.StockFixture;
import nexters.payout.domain.stock.Sector;
import nexters.payout.domain.stock.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("SectorAnalyzer 테스트")
class SectorAnalyzerTest {

    @ParameterizedTest
    @ValueSource(strings = {"APPL", "TSLA"})
    void 하나의_티커가_존재하는_경우_섹터비율_검증(String ticker) {
        // given
        List<Stock> stocks = List.of(StockFixture.createStock(ticker, Sector.FINANCIAL_SERVICES));
        SectorAnalyzer sectorAnalyzer = new SectorAnalyzer();

        // when
        Map<Sector, Double> actual = sectorAnalyzer.calculateSectorRatios(stocks);

        // then
        assertAll(
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(actual.get(Sector.FINANCIAL_SERVICES)).isEqualTo(1.0)
        );
    }

    @Test
    void 서로_다른_섹터를_가진_2개의_티커가_존재하는_경우_섹터비율_검증() {
        // given
        List<Stock> stocks = List.of(
                StockFixture.createStock("APPL", Sector.FINANCIAL_SERVICES),
                StockFixture.createStock("TSLA", Sector.TECHNOLOGY));
        SectorAnalyzer sectorAnalyzer = new SectorAnalyzer();

        // when
        Map<Sector, Double> actual = sectorAnalyzer.calculateSectorRatios(stocks);

        // then
        assertAll(
                () -> assertThat(actual).hasSize(2),
                () -> assertThat(actual).allSatisfy((sector, ratio) -> assertThat(ratio).isEqualTo(0.5))
        );
    }
}