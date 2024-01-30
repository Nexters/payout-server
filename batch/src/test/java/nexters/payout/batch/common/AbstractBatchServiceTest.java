package nexters.payout.batch.common;

import nexters.payout.batch.application.DividendBatchService;
import nexters.payout.batch.application.FinancialClient;
import nexters.payout.batch.application.StockBatchService;
import nexters.payout.domain.dividend.repository.DividendRepository;
import nexters.payout.domain.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractBatchServiceTest {
    @MockBean
    public FinancialClient financialClient;

    @Autowired
    public StockRepository stockRepository;

    @Autowired
    public DividendRepository dividendRepository;

    @Autowired
    public StockBatchService stockBatchService;

    @Autowired
    public DividendBatchService dividendBatchService;

    @AfterEach
    void afterEach() {
        dividendRepository.deleteAll();
        stockRepository.deleteAll();
    }
}
