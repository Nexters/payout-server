package nexters.payout.apiserver.portfolio.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexters.payout.apiserver.portfolio.application.PortfolioQueryService;
import nexters.payout.apiserver.portfolio.application.dto.request.PortfolioRequest;
import nexters.payout.apiserver.portfolio.application.dto.response.MonthlyDividendResponse;
import nexters.payout.apiserver.portfolio.application.dto.response.PortfolioResponse;
import nexters.payout.apiserver.portfolio.application.dto.response.YearlyDividendResponse;
import nexters.payout.apiserver.stock.application.dto.response.SectorRatioResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import nexters.payout.apiserver.portfolio.application.handler.ReadPortfolioEvent;


import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolios")
@Slf4j
public class PortfolioController implements PortfolioControllerDocs {

    private final PortfolioQueryService portfolioQueryService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @PostMapping
    public ResponseEntity<PortfolioResponse> createPortfolio(@RequestBody @Valid final PortfolioRequest portfolioRequest) {
        return ResponseEntity.ok(portfolioQueryService.createPortfolio(portfolioRequest));
    }

    @GetMapping("/{id}/monthly")
    public ResponseEntity<List<MonthlyDividendResponse>> getMonthlyDividends(@PathVariable("id") final UUID portfolioId) {
        return ResponseEntity.ok(portfolioQueryService.getMonthlyDividends(portfolioId));
    }

    @GetMapping("/{id}/yearly")
    public ResponseEntity<YearlyDividendResponse> getYearlyDividends(@PathVariable("id") final UUID portfolioId) {
        return ResponseEntity.ok(portfolioQueryService.getYearlyDividends(portfolioId));
    }

    @GetMapping("/{id}/sector-ratio")
    public ResponseEntity<List<SectorRatioResponse>> getSectorRatios(@PathVariable("id") final UUID portfolioId) {
        List<SectorRatioResponse> result = portfolioQueryService.analyzeSectorRatio(portfolioId);
        applicationEventPublisher.publishEvent(new ReadPortfolioEvent(portfolioId));
        return ResponseEntity.ok(result);
    }
}
