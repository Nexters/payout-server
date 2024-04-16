package nexters.payout.apiserver.dividend.presentation;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import nexters.payout.apiserver.dividend.application.dto.request.DividendRequest;
import nexters.payout.apiserver.dividend.application.dto.request.TickerShare;
import nexters.payout.apiserver.dividend.application.dto.response.MonthlyDividendResponse;
import nexters.payout.apiserver.dividend.application.dto.response.YearlyDividendResponse;
import nexters.payout.apiserver.dividend.common.IntegrationTest;
import nexters.payout.core.exception.ErrorResponse;
import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.DividendFixture;
import nexters.payout.domain.StockFixture;
import nexters.payout.domain.stock.domain.Sector;
import nexters.payout.domain.stock.domain.Stock;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static nexters.payout.domain.StockFixture.AAPL;
import static nexters.payout.domain.StockFixture.TSLA;
import static org.apache.http.HttpStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DividendControllerTest extends IntegrationTest {

    @Test
    void 월별_배당금_조회시_티커를_찾을수없는경우_404_예외가_발생한다() {
        // given
        stockRepository.save(StockFixture.createStock(TSLA, Sector.CONSUMER_CYCLICAL));

        // when, then
        RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request())
                .when().post("api/dividends/monthly")
                .then().log().all()
                .statusCode(SC_NOT_FOUND)
                .extract()
                .as(ErrorResponse.class);
    }

    @Test
    void 연간_배당금_조회시_티커를_찾을수없는경우_404_예외가_발생한다() {
        // given
        stockRepository.save(StockFixture.createStock(TSLA, Sector.CONSUMER_CYCLICAL));

        // when, then
        RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request())
                .when().post("api/dividends/yearly")
                .then().log().all()
                .statusCode(SC_NOT_FOUND)
                .extract()
                .as(ErrorResponse.class);
    }

    @Test
    void 월별_배당금_조회시_배당금이_존재하지_않는_경우_정상적으로_조회된다() {
        // given
        stockRepository.save(StockFixture.createStock(TSLA, Sector.CONSUMER_CYCLICAL));
        stockRepository.save(StockFixture.createStock(AAPL, Sector.TECHNOLOGY));
        double expected = 0.0;

        // when
        List<MonthlyDividendResponse> actual = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .request()
                .body(request())
                .when().post("api/dividends/monthly")
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
    void 연간_배당금_조회시_배당금이_존재하지_않는_경우_정상적으로_조회된다() {
        // given
        stockRepository.save(StockFixture.createStock(TSLA, Sector.CONSUMER_CYCLICAL));
        stockRepository.save(StockFixture.createStock(AAPL, Sector.TECHNOLOGY));
        double expected = 0.0;

        // when
        YearlyDividendResponse actual = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request())
                .when().post("api/dividends/yearly")
                .then().log().all()
                .statusCode(SC_OK)
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertAll(
                () -> assertThat(actual.totalDividend()).isEqualTo(expected),
                () -> assertThat(actual.dividends()).isEmpty()
        );
    }

    @Test
    void 월별_배당금_조회시_배당금이_존재하는_경우_정상적으로_조회된다() {
        // given
        stockAndDividendGiven();
        double expected = 13.0;

        // when
        List<MonthlyDividendResponse> actual = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request())
                .when().post("api/dividends/monthly")
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
                () -> assertThat(actual).hasSize(12)
        );
    }

    @Test
    void 연간_배당금_조회시_배당금이_존재하는_경우_정상적으로_조회된다() {
        // given
        stockAndDividendGiven();
        double expected = 13.0;

        // when
        YearlyDividendResponse actual = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request())
                .when().post("api/dividends/yearly")
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

    @Test
    void 월별_배당금_조회시_빈_리스트로_요청한_경우_400_예외가_발생한다() {
        // given
        DividendRequest request = new DividendRequest(new ArrayList<>());

        // when, then
        RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("api/dividends/monthly")
                .then().log().all()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ErrorResponse.class);
    }

    @Test
    void 연간_배당금_조회시_빈_리스트로_요청한_경우_400_예외가_발생한다() {
        // given
        DividendRequest request = new DividendRequest(new ArrayList<>());

        // when, then
        RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("api/dividends/yearly")
                .then().log().all()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ErrorResponse.class);
    }

    @Test
    void 월간_배당금_조회시_티커가_빈문자열이면_예외가_발생한다() {
        // given
        DividendRequest request = new DividendRequest(List.of(new TickerShare("", 2)));

        // when, then
        RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("api/dividends/monthly")
                .then().log().all()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ErrorResponse.class);
    }

    @Test
    void 연간_배당금_조회시_티커가_빈문자열이면_예외가_발생한다() {
        // given
        DividendRequest request = new DividendRequest(List.of(new TickerShare("", 2)));

        // when, then
        RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("api/dividends/yearly")
                .then().log().all()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ErrorResponse.class);
    }

    @Test
    void 월간_배당금_조회시__종목_소유_개수가_0개인_경우_400_예외가_발생한다() {
        // given
        DividendRequest request = new DividendRequest(List.of(new TickerShare(AAPL, 0)));

        // when, then
        RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("api/dividends/monthly")
                .then().log().all()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ErrorResponse.class);
    }

    @Test
    void 연간_배당금_조회시__종목_소유_개수가_0개인_경우_400_예외가_발생한다() {
        // given
        DividendRequest request = new DividendRequest(List.of(new TickerShare(AAPL, 0)));

        // when, then
        RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("api/dividends/yearly")
                .then().log().all()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ErrorResponse.class);
    }

    private DividendRequest request() {
        return new DividendRequest(List.of(
                new TickerShare(AAPL, 2),
                new TickerShare(TSLA, 1)
        ));
    }

    private void stockAndDividendGiven() {
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
    }

    private Instant parseDate(int year, int month) {
        LocalDate date = LocalDate.of(year, month, 1);
        ZonedDateTime zonedDateTime = date.atStartOfDay(ZoneId.of("UTC"));
        return zonedDateTime.toInstant();
    }
}