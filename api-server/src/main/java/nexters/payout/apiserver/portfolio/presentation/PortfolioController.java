package nexters.payout.apiserver.portfolio.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexters.payout.apiserver.portfolio.application.PortfolioQueryService;
import nexters.payout.apiserver.portfolio.application.dto.request.PortfolioRequest;
import nexters.payout.apiserver.portfolio.application.dto.response.PortfolioResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolios")
@Slf4j
public class PortfolioController implements PortfolioControllerDocs {

    private final PortfolioQueryService portfolioQueryService;

    @PostMapping
    public ResponseEntity<PortfolioResponse> createPortfolio(@RequestBody @Valid final PortfolioRequest portfolioRequest) {
        return ResponseEntity.ok(portfolioQueryService.createPortfolio(portfolioRequest));
    }
}
