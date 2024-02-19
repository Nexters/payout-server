package nexters.payout.apiserver.stock.presentation.integration;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import nexters.payout.apiserver.stock.application.dto.request.SectorRatioRequest;
import nexters.payout.apiserver.stock.application.dto.request.TickerShare;
import nexters.payout.apiserver.stock.application.dto.response.SectorRatioResponse;
import nexters.payout.apiserver.stock.application.dto.response.StockDetailResponse;
import nexters.payout.apiserver.stock.application.dto.response.StockResponse;
import nexters.payout.apiserver.stock.common.IntegrationTest;
import nexters.payout.core.exception.ErrorResponse;
import nexters.payout.domain.DividendFixture;
import nexters.payout.domain.StockFixture;
import nexters.payout.domain.stock.domain.Sector;
import nexters.payout.domain.stock.domain.Stock;
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
    void 검색키워드가_빈값인_경우_400_예외가_발생한다() {
        // given
        Stock apdd = StockFixture.createStock("APDD", "DDDD");

        stockRepository.save(apdd);

        // when, then
        RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .when().get("api/stocks/search?keyword=")
                .then().log().all()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);
    }

    @Test
    void 티커는_앞자리부터_검색_회사명은_중간에서도_검색_가능하다() {
        // given
        Stock apdd = StockFixture.createStock("APDD", "DDDD");
        Stock abcd = StockFixture.createStock("ABCD", "APPLE");

        stockRepository.save(apdd);
        stockRepository.save(abcd);

        // when
        List<StockResponse> actual = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .when().get("api/stocks/search?keyword=AP")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertAll(
                () -> assertThat(actual).hasSize(2),
                () -> assertThat(actual).containsExactlyInAnyOrderElementsOf(
                        List.of(
                                new StockResponse(apdd.getId(), apdd.getTicker(), apdd.getName(), apdd.getSector().getName(), apdd.getExchange(), apdd.getIndustry(), apdd.getPrice(), apdd.getVolume(), apdd.getLogoUrl()),
                                new StockResponse(abcd.getId(), abcd.getTicker(), abcd.getName(), abcd.getSector().getName(), abcd.getExchange(), abcd.getIndustry(), abcd.getPrice(), abcd.getVolume(), abcd.getLogoUrl())
                        )
                )
        );
    }

    @Test
    void 티커_기반_검색_1순위_회사명_기반_검색이_2순위이다() {
        // given
        Stock apdd = StockFixture.createStock("APDD", "DDDD");
        Stock abcd = StockFixture.createStock("ABCD", "APPLE");

        stockRepository.save(apdd);
        stockRepository.save(abcd);

        // when
        List<StockResponse> actual = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .when().get("api/stocks/search?keyword=AP")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertAll(
                () -> assertThat(actual).hasSize(2),
                () -> assertThat(actual).isEqualTo(
                        List.of(
                                new StockResponse(apdd.getId(), apdd.getTicker(), apdd.getName(), apdd.getSector().getName(), apdd.getExchange(), apdd.getIndustry(), apdd.getPrice(), apdd.getVolume(), apdd.getLogoUrl()),
                                new StockResponse(abcd.getId(), abcd.getTicker(), abcd.getName(), abcd.getSector().getName(), abcd.getExchange(), abcd.getIndustry(), abcd.getPrice(), abcd.getVolume(), abcd.getLogoUrl())
                        )
                )
        );
    }

    @Test
    void 검색_결과는_알파벳_순으로_정렬한다() {
        // given
        Stock dddd = StockFixture.createStock("DDDD", "DDDDA");
        Stock aaaa = StockFixture.createStock("AAAA", "AAADA");

        stockRepository.save(dddd);
        stockRepository.save(aaaa);

        // when
        List<StockResponse> actual = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .when().get("api/stocks/search?keyword=DA")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertAll(
                () -> assertThat(actual).hasSize(2),
                () -> assertThat(actual).containsExactlyInAnyOrderElementsOf(
                        List.of(
                                new StockResponse(aaaa.getId(), aaaa.getTicker(), aaaa.getName(), aaaa.getSector().getName(), aaaa.getExchange(), aaaa.getIndustry(), aaaa.getPrice(), aaaa.getVolume(), aaaa.getLogoUrl()),
                                new StockResponse(dddd.getId(), dddd.getTicker(), dddd.getName(), dddd.getSector().getName(), dddd.getExchange(), dddd.getIndustry(), dddd.getPrice(), dddd.getVolume(), dddd.getLogoUrl())
                        )
                )
        );
    }

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
    void 종목_조회시_종목의_정보가_정상적으로_조회된다() {
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
                () -> assertThat(actual.get(0).stocks().get(0).ticker()).isEqualTo(AAPL)
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
                () -> assertThat(actual.get(0).stocks().get(0).ticker()).isEqualTo(AAPL)
        );
    }
}