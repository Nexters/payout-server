package nexters.payout.apiserver.stock.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import nexters.payout.apiserver.stock.application.StockQueryService;
import nexters.payout.apiserver.stock.application.dto.request.SectorRatioRequest;
import nexters.payout.apiserver.stock.application.dto.response.*;
import nexters.payout.domain.stock.domain.Sector;
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
            @RequestParam @NotEmpty final String keyword,
            @RequestParam @NotNull final Integer pageNumber,
            @RequestParam @NotNull final Integer pageSize
    ) {
        return ResponseEntity.ok(stockQueryService.searchStock(keyword, pageNumber, pageSize));
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<StockDetailResponse> getStockByTicker(
            @PathVariable final String ticker
    ) {
        return ResponseEntity.ok(stockQueryService.getStockByTicker(ticker));
    }


    @PostMapping("/sector-ratio")
    public ResponseEntity<List<SectorRatioResponse>> findSectorRatios(
            @Valid @RequestBody final SectorRatioRequest request
    ) {
        return ResponseEntity.ok(stockQueryService.analyzeSectorRatio(request));
    }

    @GetMapping("/ex-dividend-dates/upcoming")
    public ResponseEntity<UpcomingDividendResponse> getUpComingDividendStocks(
            @RequestParam @NotNull final Sector sector,
            @RequestParam @NotNull final Integer pageNumber,
            @RequestParam @NotNull final Integer pageSize
    ) {
        return ResponseEntity.ok(stockQueryService.getUpcomingDividendStocks(sector, pageNumber, pageSize));
    }

    @GetMapping("/dividend-yields/highest")
    public ResponseEntity<StockDividendYieldResponse> getBiggestDividendYieldStocks(
            @RequestParam @NotNull final Sector sector,
            @RequestParam @NotNull final Integer pageNumber,
            @RequestParam @NotNull final Integer pageSize
    ) {
        return ResponseEntity.ok(stockQueryService.getBiggestDividendStocks(sector, pageNumber, pageSize));
    }
}
