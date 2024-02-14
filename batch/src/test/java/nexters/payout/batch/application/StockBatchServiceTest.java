package nexters.payout.batch.application;

import nexters.payout.batch.common.AbstractBatchServiceTest;
import nexters.payout.domain.StockFixture;
import nexters.payout.domain.stock.domain.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@DisplayName("주식 스케쥴러 서비스 테스트")
class StockBatchServiceTest extends AbstractBatchServiceTest {

    @Test
    void 현재가와_거래량을_업데이트한다() {
        // given
        Stock stock = stockRepository.save(StockFixture.createStock(StockFixture.TSLA, 10.0, 1234));
        FinancialClient.StockData stockData = LatestStockFixture.createStockData(stock.getTicker(), 30.0, 4321);
        given(financialClient.getLatestStockList()).willReturn(List.of(stockData));

        // when
        stockBatchService.run();

        // then
        Stock actual = stockRepository.findByTicker(stock.getTicker()).get();
        assertAll(
                () -> assertThat(actual.getPrice()).isEqualTo(stockData.price()),
                () -> assertThat(actual.getVolume()).isEqualTo(stockData.volume())
        );
    }
}