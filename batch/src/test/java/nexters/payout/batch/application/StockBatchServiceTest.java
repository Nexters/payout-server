package nexters.payout.batch.application;

import nexters.payout.domain.stock.Stock;
import nexters.payout.domain.stock.repository.StockRepository;
import nexters.payout.domain.StockFixture;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
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

    @DisplayName("현재가와 거래량을 업데이트한다.")
    @Test
    void runTest() {
        // given
        Stock stock = stockRepository.save(StockFixture.createStock(StockFixture.TESLA, 10.0, 1234));
        FinancialClient.StockData stockData = LatestStockFixture.createLatestStock(stock.getTicker(), 30.0, 4321);
        given(financialClient.getLatestStockList()).willReturn(List.of(stockData));

        // when
        stockBatchService.run();

        // then
        Stock actual = stockRepository.findByTicker(stock.getTicker()).get();
        assertThat(actual.getPrice()).isEqualTo(stockData.price());
        assertThat(actual.getVolume()).isEqualTo(stockData.volume());
    }
}