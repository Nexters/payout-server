package nexters.payout.apiserver.stock.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import nexters.payout.apiserver.stock.application.StockQueryService;
import nexters.payout.apiserver.stock.application.dto.request.SectorRatioRequest;
import nexters.payout.apiserver.stock.application.dto.response.SectorRatioResponse;
import nexters.payout.apiserver.stock.application.dto.response.StockDetailResponse;
import nexters.payout.apiserver.stock.application.dto.response.StockResponse;
import nexters.payout.core.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/stocks")
public class StockController implements StockControllerDocs {

    private final StockQueryService stockQueryService;

    @GetMapping("/search")
    public ResponseEntity<List<StockResponse>> searchStock(
            @RequestParam @NotEmpty final String keyword
    ) {
        return ResponseEntity.ok(stockQueryService.searchStock(keyword));
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<StockDetailResponse> getStockByTicker(
            @PathVariable final String ticker
    ) {
        return ResponseEntity.ok(stockQueryService.getStockByTicker(ticker));
    }


    @PostMapping("/sector-ratio")
    public ResponseEntity<List<SectorRatioResponse>> findSectorRatios(
            @Valid @RequestBody final SectorRatioRequest request) {
        return ResponseEntity.ok(stockQueryService.analyzeSectorRatio(request));
    }
}
