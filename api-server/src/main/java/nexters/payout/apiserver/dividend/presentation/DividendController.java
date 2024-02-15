package nexters.payout.apiserver.dividend.presentation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexters.payout.apiserver.dividend.application.DividendQueryService;
import nexters.payout.apiserver.dividend.application.dto.request.DividendRequest;
import nexters.payout.apiserver.dividend.application.dto.response.MonthlyDividendResponse;
import nexters.payout.apiserver.dividend.application.dto.response.YearlyDividendResponse;
import nexters.payout.core.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 배당금 관련 컨트롤러 클래스입니다.
 *
 * @author Min Ho CHO
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/dividends")
public class DividendController {

    private final DividendQueryService dividendQueryService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "SERVER ERROR",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/monthly")
    public ResponseEntity<List<MonthlyDividendResponse>> getMonthlyDividends(@Valid DividendRequest request) {

        return ResponseEntity.ok(dividendQueryService.getMonthlyDividends(request));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "SERVER ERROR",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/yearly")
    public ResponseEntity<YearlyDividendResponse> getYearlyDividends(@Valid DividendRequest request) {

        return ResponseEntity.ok(dividendQueryService.getYearlyDividends(request));
    }


}
