package nexters.payout.apiserver.dividend.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import nexters.payout.apiserver.dividend.application.dto.request.DividendRequest;
import nexters.payout.apiserver.dividend.application.dto.response.MonthlyDividendResponse;
import nexters.payout.apiserver.dividend.application.dto.response.YearlyDividendResponse;
import nexters.payout.core.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface DividendControllerDocs {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "SERVER ERROR",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @Operation(summary = "월별 배당금 조회",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DividendRequest.class),
                            examples = {
                                    @ExampleObject(name = "DividendRequestExample", value = "{\"tickerShares\":[{\"ticker\":\"AAPL\",\"share\":3}]}")
                            })))
    ResponseEntity<List<MonthlyDividendResponse>> getMonthlyDividends(@RequestBody @Valid DividendRequest request);


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "SERVER ERROR",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @Operation(summary = "연간 배당금 조회",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DividendRequest.class),
                            examples = {
                                    @ExampleObject(name = "DividendRequestExample", value = "{\"tickerShares\":[{\"ticker\":\"AAPL\",\"share\":3}]}")
                            })))
    ResponseEntity<YearlyDividendResponse> getYearlyDividends(@RequestBody @Valid DividendRequest request);
}

