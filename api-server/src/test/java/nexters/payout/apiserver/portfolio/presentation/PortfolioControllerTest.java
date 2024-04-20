package nexters.payout.apiserver.portfolio.presentation;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import nexters.payout.apiserver.portfolio.application.dto.response.MonthlyDividendResponse;
import nexters.payout.apiserver.portfolio.application.dto.request.PortfolioRequest;
import nexters.payout.apiserver.portfolio.application.dto.request.TickerShare;
import nexters.payout.apiserver.portfolio.application.dto.response.SectorRatioResponse;
import nexters.payout.apiserver.portfolio.application.dto.response.YearlyDividendResponse;
import nexters.payout.apiserver.portfolio.common.IntegrationTest;
import nexters.payout.core.exception.ErrorResponse;
import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.DividendFixture;
import nexters.payout.domain.PortfolioFixture;
import nexters.payout.domain.StockFixture;
import nexters.payout.domain.portfolio.domain.Portfolio;
import nexters.payout.domain.portfolio.domain.PortfolioStock;
import nexters.payout.domain.stock.domain.Sector;
import nexters.payout.domain.stock.domain.Stock;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static nexters.payout.domain.StockFixture.AAPL;
import static nexters.payout.domain.StockFixture.TSLA;
import static org.apache.http.HttpStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class PortfolioControllerTest extends IntegrationTest {

    @Test
    void 포트폴리오_생성시_티커를_찾을수_없는경우_404_예외가_발생한다() {
        // given
        stockRepository.save(StockFixture.createStock(TSLA, Sector.CONSUMER_CYCLICAL));

        // when, then
        RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request())
                .when().post("api/portfolios")
                .then().log().all()
                .statusCode(SC_NOT_FOUND)
                .extract()
                .as(ErrorResponse.class);
    }

    @Test
    void 포트폴리오_생성시_빈_리스트로_요청한_경우_400_예외가_발생한다() {
        // given
        PortfolioRequest request = new PortfolioRequest(new ArrayList<>());

        // when, then
        RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("api/portfolios")
                .then().log().all()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ErrorResponse.class);
    }

    @Test
    void 포트폴리오_생성시_티커가_빈문자열이면_400_예외가_발생한다() {
        // given
        PortfolioRequest request = new PortfolioRequest(List.of(new TickerShare("", 2)));

        // when, then
        RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("api/portfolios")
                .then().log().all()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ErrorResponse.class);
    }

    @Test
    void 포트폴리오_생성시__종목_소유_개수가_0개인_경우_400_예외가_발생한다() {
        // given
        stockRepository.save(StockFixture.createStock(TSLA, Sector.CONSUMER_CYCLICAL));
        PortfolioRequest request = new PortfolioRequest(List.of(new TickerShare(TSLA, 0)));

        // when, then
        RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("api/portfolios")
                .then().log().all()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ErrorResponse.class);
    }

    @Test
    void 사용자의_섹터_비중을_분석한다() {
        // given
        Stock tsla = stockRepository.save(StockFixture.createStock(TSLA, Sector.CONSUMER_CYCLICAL, 10.0));
        Stock aapl = stockRepository.save(StockFixture.createStock(AAPL, Sector.TECHNOLOGY, 20.0));
        Portfolio portfolio = portfolioRepository.save(PortfolioFixture.createPortfolio(
                        List.of(new PortfolioStock(tsla.getId(), 1), new PortfolioStock(aapl.getId(), 1))
                )
        );

        // when
        List<SectorRatioResponse> actual = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request())
                .when().get(String.format("api/portfolios/%s/sector-ratio", portfolio.getId()))
                .then().log().all()
                .statusCode(SC_OK)
                .extract()
                .as(new TypeRef<>() {
                });

        List<SectorRatioResponse> sorted = actual.stream()
                .sorted(Comparator.comparing(SectorRatioResponse::sectorRatio))
                .toList();
        // then
        assertAll(
                () -> assertThat(sorted).hasSize(2),
                () -> assertThat(sorted.get(0).sectorRatio()).isCloseTo(0.33, Offset.offset(0.01)),
                () -> assertThat(sorted.get(0).sectorName()).isEqualTo(Sector.CONSUMER_CYCLICAL.getName()),
                () -> assertThat(sorted.get(1).sectorRatio()).isCloseTo(0.66, Offset.offset(0.01)),
                () -> assertThat(sorted.get(1).sectorName()).isEqualTo(Sector.TECHNOLOGY.getName())
        );
    }

    @Test
    void 월별_배당금_조회시_배당금이_존재하지_않는_경우_정상적으로_조회된다() {
        // given
        Stock tsla = stockRepository.save(StockFixture.createStock(TSLA, Sector.CONSUMER_CYCLICAL));
        Stock aapl = stockRepository.save(StockFixture.createStock(AAPL, Sector.TECHNOLOGY));
        Portfolio portfolio = portfolioRepository.save(PortfolioFixture.createPortfolio(
                        LocalDate.now().plusMonths(1).atStartOfDay().toInstant(ZoneOffset.UTC),
                        List.of(new PortfolioStock(tsla.getId(), 2), new PortfolioStock(aapl.getId(), 1))
                )
        );
        double expected = 0.0;

        // when
        List<MonthlyDividendResponse> actual = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .request()
                .body(request())
                .when().get(String.format("api/portfolios/%s/monthly", portfolio.getId()))
                .then().log().all()
                .statusCode(SC_OK)
                .extract()
                .as(new TypeRef<>() {
                });

        assertAll(
                () -> assertThat(actual
                        .stream()
                        .mapToDouble(MonthlyDividendResponse::totalDividend)
                        .sum())
                        .isEqualTo(expected),
                () -> actual.forEach(res -> assertThat(res.dividends()).isEmpty())
        );
    }

    @Test
    void 월별_배당금_조회시_배당금이_존재하는_경우_정상적으로_조회된다() {
        // given
        Portfolio portfolio = stockAndDividendAndPortfolioGiven();
        double expected = 13.0;

        // when
        List<MonthlyDividendResponse> actual = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .request()
                .body(request())
                .when().get(String.format("api/portfolios/%s/monthly", portfolio.getId()))
                .then().log().all()
                .statusCode(SC_OK)
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertAll(
                () -> assertThat(actual
                        .stream()
                        .mapToDouble(MonthlyDividendResponse::totalDividend)
                        .sum())
                        .isEqualTo(expected),
                () -> assertThat(actual).hasSize(12)
        );
    }

    @Test
    void 연간_배당금_조회시_배당금이_존재하지_않는_경우_정상적으로_조회된다() {
        // given
        Stock tsla = stockRepository.save(StockFixture.createStock(TSLA, Sector.CONSUMER_CYCLICAL));
        Stock aapl = stockRepository.save(StockFixture.createStock(AAPL, Sector.TECHNOLOGY));
        Portfolio portfolio = portfolioRepository.save(PortfolioFixture.createPortfolio(
                        LocalDate.now().plusMonths(1).atStartOfDay().toInstant(ZoneOffset.UTC),
                        List.of(new PortfolioStock(tsla.getId(), 2), new PortfolioStock(aapl.getId(), 1))
                )
        );
        double expected = 0.0;

        // when
        YearlyDividendResponse actual = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .request()
                .body(request())
                .when().get(String.format("api/portfolios/%s/yearly", portfolio.getId()))
                .then().log().all()
                .statusCode(SC_OK)
                .extract()
                .as(new TypeRef<>() {
                });

        assertAll(
                () -> assertThat(actual.totalDividend()).isEqualTo(expected),
                () -> assertThat(actual.dividends()).isEmpty()
        );
    }

    @Test
    void 연간_배당금_조회시_배당금이_존재하는_경우_정상적으로_조회된다() {
        // given
        Portfolio portfolio = stockAndDividendAndPortfolioGiven();
        double expected = 13.0;

        // when
        YearlyDividendResponse actual = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .request()
                .body(request())
                .when().get(String.format("api/portfolios/%s/yearly", portfolio.getId()))
                .then().log().all()
                .statusCode(SC_OK)
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertAll(
                () -> assertThat(actual.totalDividend()).isEqualTo(expected),
                () -> assertThat(actual.dividends().size()).isEqualTo(2)
        );
    }

    private PortfolioRequest request() {
        return new PortfolioRequest(List.of(
                new TickerShare(AAPL, 2),
                new TickerShare(TSLA, 2)
        ));
    }

    private Portfolio stockAndDividendAndPortfolioGiven() {
        Stock aapl = stockRepository.save(StockFixture.createStock(AAPL, Sector.TECHNOLOGY));
        Stock tsla = stockRepository.save(StockFixture.createStock(TSLA, Sector.CONSUMER_CYCLICAL));

        dividendRepository.save(DividendFixture.createDividend(
                aapl.getId(),
                2.5,
                parseDate(InstantProvider.getLastYear(), 1)));
        dividendRepository.save(DividendFixture.createDividend(
                aapl.getId(),
                2.5,
                parseDate(InstantProvider.getLastYear(), 6)));
        dividendRepository.save(DividendFixture.createDividend(
                tsla.getId(),
                3.0,
                parseDate(InstantProvider.getLastYear(), 6)));

        return portfolioRepository.save(PortfolioFixture.createPortfolio(
                        LocalDate.now().plusMonths(1).atStartOfDay().toInstant(ZoneOffset.UTC),
                        List.of(new PortfolioStock(aapl.getId(), 2), new PortfolioStock(tsla.getId(), 1))
                )
        );
    }

    private Instant parseDate(int year, int month) {
        LocalDate date = LocalDate.of(year, month, 1);
        ZonedDateTime zonedDateTime = date.atStartOfDay(ZoneId.of("UTC"));
        return zonedDateTime.toInstant();
    }
}
