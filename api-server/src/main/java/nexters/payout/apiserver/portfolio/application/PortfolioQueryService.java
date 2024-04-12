package nexters.payout.apiserver.portfolio.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexters.payout.apiserver.portfolio.application.dto.request.PortfolioRequest;
import nexters.payout.apiserver.portfolio.application.dto.response.PortfolioResponse;
import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.portfolio.application.PortfolioCommandService;
import nexters.payout.domain.portfolio.domain.Portfolio;
import nexters.payout.domain.portfolio.domain.PortfolioStock;
import nexters.payout.domain.stock.domain.exception.TickerNotFoundException;
import nexters.payout.domain.stock.domain.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PortfolioQueryService {

    private final PortfolioCommandService portfolioCommandService;
    private final StockRepository stockRepository;

    public PortfolioResponse createPortfolio(final PortfolioRequest request) {

        List<PortfolioStock> portfolioStocks =
                request.tickerShares()
                        .stream().map(tickerShare -> new PortfolioStock(
                                stockRepository.findByTicker(tickerShare.ticker()).orElseThrow(
                                        () -> new TickerNotFoundException(tickerShare.ticker())).getId(),
                                tickerShare.share()))
                        .toList();

        return new PortfolioResponse(portfolioCommandService.createPortfolio(
                new Portfolio(
                        InstantProvider.getExpireAt(),
                        portfolioStocks
                )
        ));
    }
}
