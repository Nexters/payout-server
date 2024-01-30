package nexters.payout.batch.application;

import nexters.payout.batch.common.annotation.BatchServiceTest;
import nexters.payout.domain.StockFixture;
import nexters.payout.domain.stock.Stock;
import nexters.payout.domain.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;


@BatchServiceTest
@DisplayName("주식 스케쥴러 서비스 테스트")
class StockBatchServiceTest {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockBatchService stockBatchService;

    @MockBean
    private FinancialClient financialClient;

    @AfterEach
    void afterEach() {
        stockRepository.deleteAll();
    }

    @DisplayName("현재가와 거래량을 업데이트한다")
    @Test
    void 현재가와_거래량을_업데이트한다() {
        // given
        Stock stock = stockRepository.save(StockFixture.createStock(StockFixture.TESLA, 10.0, 1234));
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