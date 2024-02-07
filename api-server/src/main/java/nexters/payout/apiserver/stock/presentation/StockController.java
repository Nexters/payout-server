package nexters.payout.apiserver.stock.presentation;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import nexters.payout.apiserver.stock.application.StockService;
import nexters.payout.apiserver.stock.application.dto.response.SectorRatioResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/stocks/api")
public class StockController {

    private final StockService stockService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST")
    })
    @GetMapping("/sector-ratio")
    public ResponseEntity<List<SectorRatioResponse>> findSectorRatios(
            @Size(min = 1) @RequestParam List<String> tickers) {
        return ResponseEntity.ok(stockService.findSectorRatios(tickers));
    }
}
