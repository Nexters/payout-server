package nexters.payout.apiserver.stock.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import nexters.payout.apiserver.dividend.application.dto.request.DividendRequest;
import nexters.payout.apiserver.stock.application.dto.request.SectorRatioRequest;
import nexters.payout.apiserver.stock.application.dto.response.*;
import nexters.payout.core.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface StockControllerDocs {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "SERVER ERROR",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @Operation(summary = "티커명/회사명 검색",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StockResponse.class),
                            examples = {
                                    @ExampleObject(name = "StockResponse")
                            })))
    ResponseEntity<List<StockResponse>> searchStock(
            @Parameter(description = "tickerName or companyName of stock ex) APPL, APPLE", required = true)
            @RequestParam @NotEmpty String ticker,
            @Parameter(description = "page number(start with 1) for pagination", example = "1", required = true)
            @RequestParam @NotNull final Integer pageNumber,
            @Parameter(description = "page size for pagination", example = "20", required = true)
            @RequestParam @NotNull final Integer pageSize
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
    @Operation(summary = "종목 상세 조회",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StockDetailResponse.class),
                            examples = {
                                    @ExampleObject(name = "StockDetailResponse")
                            })))
    ResponseEntity<StockDetailResponse> getStockByTicker(
            @Parameter(description = "tickerName of stock", example = "AAPL", required = true)
            @PathVariable String ticker
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
    @Operation(summary = "섹터 비중 분석",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SectorRatioResponse.class),
                            examples = {
                                    @ExampleObject(name = "SectorRatioResponse")
                            })))
    ResponseEntity<List<SectorRatioResponse>> findSectorRatios(
            @Valid @RequestBody final SectorRatioRequest request);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "SERVER ERROR",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @Operation(summary = "배당락일이 다가오는 주식 리스트",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpcomingDividendResponse.class),
                            examples = {
                                    @ExampleObject(name = "UpcomingDividendResponse")
                            })))
    ResponseEntity<List<UpcomingDividendResponse>> getUpComingDividendStocks(
            @Parameter(description = "page number(start with 1) for pagination", example = "1", required = true)
            @RequestParam @NotNull final Integer pageNumber,
            @Parameter(description = "page size for pagination", example = "20", required = true)
            @RequestParam @NotNull final Integer pageSize
    );

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "SERVER ERROR",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @Operation(summary = "배당수익률이 큰 주식 리스트",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StockDividendYieldResponse.class),
                            examples = {
                                    @ExampleObject(name = "StockDividendYieldResponse")
                            })))
    ResponseEntity<List<StockDividendYieldResponse>> getBiggestDividendYieldStocks(
            @Parameter(description = "page number(start with 1) for pagination", example = "1", required = true)
            @RequestParam @NotNull final Integer pageNumber,
            @Parameter(description = "page size for pagination", example = "20", required = true)
            @RequestParam @NotNull final Integer pageSize
    );
}

