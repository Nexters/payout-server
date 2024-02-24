package nexters.payout.apiserver.stock.presentation.integration;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import nexters.payout.apiserver.stock.application.dto.request.SectorRatioRequest;
import nexters.payout.apiserver.stock.application.dto.request.TickerShare;
import nexters.payout.apiserver.stock.application.dto.response.*;
import nexters.payout.apiserver.stock.common.IntegrationTest;
import nexters.payout.core.exception.ErrorResponse;
import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.DividendFixture;
import nexters.payout.domain.StockFixture;
import nexters.payout.domain.stock.domain.Sector;
import nexters.payout.domain.stock.domain.Stock;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static nexters.payout.core.time.InstantProvider.*;
import static nexters.payout.domain.StockFixture.*;

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
                .when().get("api/stocks/search?keyword=AP&pageNumber=1&pageSize=20")
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
                .when().get("api/stocks/search?keyword=AP&pageNumber=1&pageSize=20")
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
                .when().get("api/stocks/search?keyword=DA&pageNumber=1&pageSize=20")
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
                () -> assertThat(actual.get(0).stockShares().get(0).stockResponse().ticker()).isEqualTo(AAPL)
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
                () -> assertThat(actual.get(0).stockShares().get(0).stockResponse().ticker()).isEqualTo(AAPL)
        );
    }

    @Test
    void 배당락일이_다가오는_주식_리스트를_가져온다() {
        // given
        Stock aapl = stockRepository.save(StockFixture.createStock(AAPL, Sector.TECHNOLOGY, 5.0));
        dividendRepository.save(DividendFixture.createDividendWithExDividendDate(
                aapl.getId(),
                25.0,
                LocalDateTime.now().plusDays(1).toInstant(UTC)
        ));
        LocalDateTime expected = LocalDateTime.now().plusDays(1);

        // when
        List<UpcomingDividendResponse> actual = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .when().get("api/stocks/ex-dividend-dates/upcoming?pageNumber=1&pageSize=20")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertAll(
                () -> assertThat(actual.size()).isEqualTo(1),
                () -> assertThat(actual.get(0).stockId()).isEqualTo(aapl.getId()),
                () -> assertThat(getYear(actual.get(0).exDividendDate())).isEqualTo(expected.getYear()),
                () -> assertThat(getMonth(actual.get(0).exDividendDate())).isEqualTo(expected.getMonthValue()),
                () -> assertThat(getDayOfMonth(actual.get(0).exDividendDate())).isEqualTo(expected.getDayOfMonth())
        );
    }

    @Test
    void 배당락일이_다가오는_주식_리스트는_배당락일이_가까운_순서대로_정렬된다() {
        // given
        Stock aapl = stockRepository.save(StockFixture.createStock(AAPL, Sector.TECHNOLOGY, 5.0));
        Stock tsla = stockRepository.save(StockFixture.createStock(TSLA, Sector.TECHNOLOGY, 5.0));
        dividendRepository.save(DividendFixture.createDividendWithExDividendDate(
                aapl.getId(),
                25.0,
                LocalDateTime.now().plusDays(2).toInstant(UTC)
        ));
        dividendRepository.save(DividendFixture.createDividendWithExDividendDate(
                tsla.getId(),
                30.0,
                LocalDateTime.now().plusDays(1).toInstant(UTC)
        ));
        LocalDateTime expected = LocalDateTime.now().plusDays(1);

        // when
        List<UpcomingDividendResponse> actual = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .when().get("api/stocks/ex-dividend-dates/upcoming?pageNumber=1&pageSize=20")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertAll(
                () -> assertThat(actual.size()).isEqualTo(2),
                () -> assertThat(actual.get(0).stockId()).isEqualTo(tsla.getId()),
                () -> assertThat(getYear(actual.get(0).exDividendDate())).isEqualTo(expected.getYear()),
                () -> assertThat(getMonth(actual.get(0).exDividendDate())).isEqualTo(expected.getMonthValue()),
                () -> assertThat(getDayOfMonth(actual.get(0).exDividendDate())).isEqualTo(expected.getDayOfMonth())
        );
    }

    @Test
    void 배당_수익률이_큰_순서대로_주식_리스트를_가져온다() {
        // given
        Stock aapl = stockRepository.save(StockFixture.createStock(AAPL, Sector.TECHNOLOGY, 8.0));
        Stock tsla = stockRepository.save(StockFixture.createStock(TSLA, Sector.TECHNOLOGY, 20.0));
        dividendRepository.save(DividendFixture.createDividendWithExDividendDate(
                aapl.getId(),
                8.0,
                LocalDate.of(InstantProvider.getLastYear(), 3, 1).atStartOfDay().toInstant(UTC)
        ));
        dividendRepository.save(DividendFixture.createDividendWithExDividendDate(
                tsla.getId(),
                5.0,
                LocalDate.of(InstantProvider.getLastYear(), 3, 1).atStartOfDay().toInstant(UTC)
        ));
        dividendRepository.save(DividendFixture.createDividendWithExDividendDate(
                tsla.getId(),
                5.0,
                LocalDate.of(InstantProvider.getLastYear(), 6, 1).atStartOfDay().toInstant(UTC)
        ));
        dividendRepository.save(DividendFixture.createDividendWithExDividendDate(
                tsla.getId(),
                5.0,
                LocalDate.of(InstantProvider.getLastYear() - 1, 6, 1).atStartOfDay().toInstant(UTC)
        ));

        Double expectedAaplDividendYield = 1.0;
        Double expectedTslaDividendYield = 2.0;

        // when
        List<StockDividendYieldResponse> actual = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .when().get("api/stocks/dividend-yields/highest?pageNumber=1&pageSize=20")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertAll(
                () -> assertThat(actual.size()).isEqualTo(2),
                () -> assertThat(actual.get(0).dividendYield()).isEqualTo(expectedTslaDividendYield),
                () -> assertThat(actual.get(0).ticker()).isEqualTo(tsla.getTicker()),
                () -> assertThat(actual.get(1).dividendYield()).isEqualTo(expectedAaplDividendYield)
        );
    }

    @Test
    void 연간_배당금이_없는_주식은_배당_수익률_계산시_포함되지_않는다() {
        // given
        Stock aapl = stockRepository.save(StockFixture.createStock(AAPL, Sector.TECHNOLOGY, 5.0));
        stockRepository.save(StockFixture.createStock(TSLA, Sector.TECHNOLOGY, 0.0));
        dividendRepository.save(DividendFixture.createDividendWithExDividendDate(
                aapl.getId(),
                5.0,
                LocalDate.of(InstantProvider.getLastYear(), 3, 1).atStartOfDay().toInstant(UTC)
        ));
        Double expected = 1.0;

        // when
        List<StockDividendYieldResponse> actual = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .when().get("api/stocks/dividend-yields/highest?pageNumber=1&pageSize=20")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertAll(
                () -> assertThat(actual.size()).isEqualTo(1),
                () -> assertThat(actual.get(0).dividendYield()).isEqualTo(expected)
        );
    }
}