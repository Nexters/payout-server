package nexters.payout.apiserver.portfolio.common;

import io.restassured.RestAssured;
import nexters.payout.domain.dividend.domain.repository.DividendRepository;
import nexters.payout.domain.portfolio.domain.repository.PortfolioRepository;
import nexters.payout.domain.stock.domain.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    public StockRepository stockRepository;

    @Autowired
    public DividendRepository dividendRepository;

    @Autowired
    public PortfolioRepository portfolioRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void afterEach() {
        dividendRepository.deleteAll();
        stockRepository.deleteAll();
        portfolioRepository.deleteAll();
    }
}
