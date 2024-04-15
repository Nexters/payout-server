package nexters.payout.apiserver.portfolio.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import nexters.payout.apiserver.portfolio.application.dto.request.PortfolioRequest;
import nexters.payout.apiserver.portfolio.application.dto.response.MonthlyDividendResponse;
import nexters.payout.apiserver.portfolio.application.dto.response.PortfolioResponse;
import nexters.payout.apiserver.portfolio.application.dto.response.YearlyDividendResponse;
import nexters.payout.core.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;


public interface PortfolioControllerDocs {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "SERVER ERROR",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @Operation(summary = "포트폴리오 생성",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PortfolioRequest.class),
                            examples = {
                                    @ExampleObject(name = "PortfolioRequestExample", value = "{\"tickerShares\":[{\"ticker\":\"AAPL\",\"share\":3}]}")
                            })))
    ResponseEntity<PortfolioResponse> createPortfolio(@RequestBody @Valid final PortfolioRequest portfolioRequest);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "SERVER ERROR",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @Operation(summary = "포트폴리오 기반 월간 배당금 조회")
    ResponseEntity<List<MonthlyDividendResponse>> getMonthlyDividends(
            @Parameter(description = "portfolio id", example = "bf5ffb6d-ae70-4171-8c86-b27c8ab2efbb", required = true)
            @PathVariable("id") final UUID portfolioId
    );

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "SERVER ERROR",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @Operation(summary = "포트폴리오 기반 연간 배당금 조회")
    ResponseEntity<YearlyDividendResponse> getYearlyDividends(
            @Parameter(description = "portfolio id", example = "bf5ffb6d-ae70-4171-8c86-b27c8ab2efbb", required = true)
            @PathVariable("id") final UUID portfolioId
    );
}
