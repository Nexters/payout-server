package nexters.payout.apiserver.portfolio.application;

import nexters.payout.apiserver.dividend.application.dto.request.TickerShare;
import nexters.payout.apiserver.portfolio.application.dto.request.PortfolioRequest;
import nexters.payout.apiserver.portfolio.application.dto.response.PortfolioResponse;
import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.PortfolioFixture;
import nexters.payout.domain.StockFixture;
import nexters.payout.domain.portfolio.domain.PortfolioStock;
import nexters.payout.domain.portfolio.domain.repository.PortfolioRepository;
import nexters.payout.domain.stock.domain.Sector;
import nexters.payout.domain.stock.domain.Stock;
import nexters.payout.domain.stock.domain.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static nexters.payout.domain.StockFixture.*;

@ExtendWith(MockitoExtension.class)
class PortfolioQueryServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private PortfolioQueryService portfolioQueryService;

    @Test
    void 포트폴리오를_생성한다() {
        // given
        Stock appl = StockFixture.createStock(AAPL, Sector.TECHNOLOGY, 4.0);
        Stock tsla = StockFixture.createStock(TSLA, Sector.CONSUMER_CYCLICAL, 2.2);
        String expected = "67221662-c2f7-4f35-9447-6a65ca88d5ea";

        given(stockRepository.findByTicker(AAPL)).willReturn(Optional.of(appl));
        given(stockRepository.findByTicker(TSLA)).willReturn(Optional.of(tsla));
        given(portfolioRepository.save(any())).willReturn(PortfolioFixture.createPortfolio(
                        UUID.fromString("67221662-c2f7-4f35-9447-6a65ca88d5ea"),
                        InstantProvider.getExpireAt(),
                        List.of(
                                new PortfolioStock(UUID.randomUUID(), 2),
                                new PortfolioStock(UUID.randomUUID(), 1)
                        )
                )
        );

        // when
        PortfolioResponse actual = portfolioQueryService.createPortfolio(request());

        // then
        assertThat(actual.id()).isEqualTo(UUID.fromString(expected));
    }

    private PortfolioRequest request() {
        return new PortfolioRequest(List.of(
                new TickerShare(AAPL, 2),
                new TickerShare(TSLA, 1))
        );
    }
}