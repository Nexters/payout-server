package nexters.payout.apiserver.stock.presentation.integration;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import nexters.payout.apiserver.stock.application.dto.request.SectorRatioRequest;
import nexters.payout.apiserver.stock.application.dto.request.TickerShare;
import nexters.payout.apiserver.stock.application.dto.response.SectorRatioResponse;
import nexters.payout.apiserver.stock.application.dto.response.StockDetailResponse;
import nexters.payout.apiserver.stock.common.IntegrationTest;
import nexters.payout.core.exception.ErrorResponse;
import nexters.payout.domain.DividendFixture;
import nexters.payout.domain.StockFixture;
import nexters.payout.domain.stock.Sector;
import nexters.payout.domain.stock.Stock;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static nexters.payout.domain.StockFixture.AAPL;
import static nexters.payout.domain.StockFixture.TSLA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class StockControllerTest extends IntegrationTest {

    @Test
    void 종목_조회시_티커를_찾을수없는경우_404_예외가_발생한다() {
        // given
        stockRepository.save(StockFixture.createStock(TSLA, Sector.CONSUMER_CYCLICAL));

        // when, then
        RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .when().get("api/stocks/aaaaa")
                .then().log().all()
                .statusCode(404)
                .extract()
                .as(ErrorResponse.class);
    }

    @Test
    void 종목_조회시_배당금이_존재하지_않는_경우_정상적으로_조회된다() {
        // given
        stockRepository.save(StockFixture.createStock(TSLA, Sector.CONSUMER_CYCLICAL));

        // when, then
        StockDetailResponse stockDetailResponse = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .when().get("api/stocks/TSLA")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        assertAll(
                () -> assertThat(stockDetailResponse.ticker()).isEqualTo(TSLA),
                () -> assertThat(stockDetailResponse.sectorName()).isEqualTo(Sector.CONSUMER_CYCLICAL.getName())
        );
    }

    @Test
    void 종목_조회시_배당금이_존재하는_경우_정상적으로_조회된다() {
        // given
        Double price = 100.0;
        Double dividend = 12.0;
        Stock tsla = stockRepository.save(StockFixture.createStock(TSLA, Sector.CONSUMER_CYCLICAL, price));
        Instant paymentDate = LocalDate.of(2023, 4, 5).atStartOfDay().toInstant(UTC);
        dividendRepository.save(DividendFixture.createDividend(tsla.getId(), dividend, paymentDate));

        // when, then
        StockDetailResponse stockDetailResponse = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .when().get("api/stocks/TSLA")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        assertAll(
                () -> assertThat(stockDetailResponse.ticker()).isEqualTo(TSLA),
                () -> assertThat(stockDetailResponse.sectorName()).isEqualTo(Sector.CONSUMER_CYCLICAL.getName()),
                () -> assertThat(stockDetailResponse.dividendYield()).isEqualTo(dividend / price),
                () -> assertThat(stockDetailResponse.earliestPaymentDate()).isEqualTo(LocalDate.of(LocalDate.now().getYear(), 4, 5)),
                () -> assertThat(stockDetailResponse.dividendMonths()).isEqualTo(List.of(Month.APRIL))
        );
    }

    @Test
    void 종목_조회시_종목의_현재가가_존재하지않으면_배당수익률은_0으로_조회된다() {
        // given
        Double price = null;
        Double dividend = 12.0;
        Stock tsla = stockRepository.save(StockFixture.createStock(TSLA, Sector.CONSUMER_CYCLICAL, price));
        Instant paymentDate = LocalDate.of(2023, 4, 5).atStartOfDay().toInstant(UTC);
        dividendRepository.save(DividendFixture.createDividend(tsla.getId(), dividend, paymentDate));

        // when, then
        StockDetailResponse stockDetailResponse = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .when().get("api/stocks/TSLA")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        assertAll(
                () -> assertThat(stockDetailResponse.dividendPerShare()).isEqualTo(dividend),
                () -> assertThat(stockDetailResponse.dividendYield()).isEqualTo(0),
                () -> assertThat(stockDetailResponse.earliestPaymentDate()).isEqualTo(LocalDate.of(LocalDate.now().getYear(), 4, 5)),
                () -> assertThat(stockDetailResponse.dividendMonths()).isEqualTo(List.of(Month.APRIL))
        );
    }


    @Test
    void 섹터_분석시_빈_리스트로_요청한_경우_400_예외가_발생한다() {
        // given
        SectorRatioRequest request = new SectorRatioRequest(List.of());

        // when, then
        RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("api/stocks/sector-ratio")
                .then().log().all()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);
    }

    @Test
    void 섹터_분석시_티커가_빈문자열이면_예외가_발생한다() {
        // given
        SectorRatioRequest request = new SectorRatioRequest(List.of(new TickerShare("", 1)));

        // when, then
        RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("api/stocks/sector-ratio")
                .then().log().all()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);
    }

    @Test
    void 섹터_분석시_종목_소유_개수가_0개인_경우_400_예외가_발생한다() {
        // given
        SectorRatioRequest request = new SectorRatioRequest(List.of(new TickerShare(AAPL, 0)));

        // when, then
        RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("api/stocks/sector-ratio")
                .then().log().all()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);
    }


    @Test
    void 섹터_분석시_티커가_1개_이상일_경우_정상적으로_동작한다() {
        // given
        SectorRatioRequest request = new SectorRatioRequest(List.of(new TickerShare(AAPL, 2)));
        Stock stock = stockRepository.save(StockFixture.createStock(AAPL, Sector.TECHNOLOGY, 5.0));
        dividendRepository.save(DividendFixture.createDividend(stock.getId(), 12.0));

        // when
        List<SectorRatioResponse> actual = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("api/stocks/sector-ratio")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertAll(
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(actual.get(0).sectorName()).isEqualTo("Technology"),
                () -> assertThat(actual.get(0).sectorRatio()).isEqualTo(1.0),
                () -> assertThat(actual.get(0).stocks().get(0).ticker()).isEqualTo(AAPL),
                () -> assertThat(actual.get(0).stocks().get(0).dividendPerShare()).isEqualTo(12.0)
        );
    }

    @Test
    void 섹터_분석시_선택한_종목의_배당금이_존재하지_않아도_정상적으로_동작한다() {
        // given
        SectorRatioRequest request = new SectorRatioRequest(List.of(new TickerShare(AAPL, 2)));
        Stock stock = stockRepository.save(StockFixture.createStock(AAPL, Sector.TECHNOLOGY, 5.0));

        // when
        List<SectorRatioResponse> actual = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("api/stocks/sector-ratio")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertAll(
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(actual.get(0).sectorName()).isEqualTo("Technology"),
                () -> assertThat(actual.get(0).sectorRatio()).isEqualTo(1.0),
                () -> assertThat(actual.get(0).stocks().get(0).ticker()).isEqualTo(AAPL),
                () -> assertThat(actual.get(0).stocks().get(0).dividendPerShare()).isEqualTo(null)
        );
    }
}