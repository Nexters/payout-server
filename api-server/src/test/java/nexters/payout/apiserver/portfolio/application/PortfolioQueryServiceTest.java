package nexters.payout.apiserver.portfolio.application;

import nexters.payout.apiserver.dividend.common.GivenFixtureTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PortfolioQueryServiceTest extends GivenFixtureTest {

    @InjectMocks
    private PortfolioQueryService portfolioQueryService;

    @Test
    void createPortfolio() {
    }
}