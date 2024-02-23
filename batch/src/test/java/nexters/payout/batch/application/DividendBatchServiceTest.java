package nexters.payout.batch.application;

import nexters.payout.batch.common.AbstractBatchServiceTest;
import nexters.payout.domain.DividendFixture;
import nexters.payout.domain.StockFixture;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.stock.domain.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static nexters.payout.core.time.InstantProvider.*;
import static nexters.payout.domain.StockFixture.AAPL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@DisplayName("배당금 스케쥴러 서비스 테스트")
class DividendBatchServiceTest extends AbstractBatchServiceTest {

    @MockBean
    DateTimeProvider dateTimeProvider;

    @SpyBean
    AuditingHandler auditingHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        auditingHandler.setDateTimeProvider(dateTimeProvider);
    }

    @Test
    void 새로운_과거_배당금_정보를_생성한다() {

        // given
        Stock stock = stockRepository.save(StockFixture.createStock(AAPL, 12.51, 120000));
        Dividend expected = DividendFixture.createDividend(stock.getId());

        List<FinancialClient.DividendData> responses = new ArrayList<>();
        responses.add(new FinancialClient.DividendData(
                Instant.parse("2023-12-21T00:00:00Z"),
                "May 31, 23",
                12.21,
                "AAPL",
                12.21,
                Instant.parse("2023-12-21T00:00:00Z"),
                Instant.parse("2023-12-23T00:00:00Z"),
                Instant.parse("2023-12-22T00:00:00Z")));

        given(financialClient.getPastDividendList()).willReturn(responses);

        // when
        dividendBatchService.updatePastDividendInfo();

        // then
        assertThat(dividendRepository.findByStockIdAndExDividendDate(
                stock.getId(),
                Instant.parse("2023-12-21T00:00:00Z")))
                .isPresent();

        Dividend actual = dividendRepository.findByStockIdAndExDividendDate(
                        stock.getId(),
                        Instant.parse("2023-12-21T00:00:00Z"))
                .get();

        assertAll(
                () -> assertThat(actual.getDividend()).isEqualTo(expected.getDividend()),
                () -> assertThat(actual.getExDividendDate()).isEqualTo(expected.getExDividendDate()),
                () -> assertThat(dividendRepository.findAll().size()).isEqualTo(1)
        );
    }

    @Test
    void 기존의_과거_배당금_정보를_갱신한다() {

        // given
        Stock stock = stockRepository.save(StockFixture.createStock(AAPL, 12.51, 120000));
        Dividend expected = dividendRepository.save(DividendFixture.createDividendWithNullDate(stock.getId()));

        List<FinancialClient.DividendData> responses = new ArrayList<>();
        responses.add(new FinancialClient.DividendData(
                Instant.parse("2023-12-21T00:00:00Z"),
                "May 31, 23",
                12.21,
                AAPL,
                12.21,
                Instant.parse("2023-12-21T00:00:00Z"),
                Instant.parse("2023-12-23T00:00:00Z"),
                Instant.parse("2023-12-22T00:00:00Z")));

        given(financialClient.getPastDividendList()).willReturn(responses);

        // when
        dividendBatchService.updatePastDividendInfo();

        // then
        Dividend actual = dividendRepository.findByStockIdAndExDividendDate(
                        stock.getId(),
                        Instant.parse("2023-12-21T00:00:00Z"))
                .get();

        assertAll(
                () -> assertThat(actual.getDividend()).isEqualTo(expected.getDividend()),
                () -> assertThat(actual.getExDividendDate()).isEqualTo(expected.getExDividendDate()),
                () -> assertThat(actual.getPaymentDate()).isEqualTo(Instant.parse("2023-12-23T00:00:00Z")),
                () -> assertThat(actual.getDeclarationDate()).isEqualTo(Instant.parse("2023-12-22T00:00:00Z")),
                () -> assertThat(dividendRepository.findAll().size()).isEqualTo(1)
        );
    }

    @Test
    void 미래_배당금_정보를_생성할때_어제_삽입된_미래_배당금_정보는_제거된다() {
        // given
        given(dateTimeProvider.getNow()).willReturn(Optional.of(LocalDateTime.now().minusDays(1)));
        Stock stock = stockRepository.save(StockFixture.createStock(AAPL, 12.51, 120000));
        dividendRepository.save(DividendFixture.createDividendWithExDividendDate(
                stock.getId(),
                21.02,
                LocalDateTime.now().toInstant(UTC)));

        given(financialClient.getUpcomingDividendList()).willReturn(new ArrayList<>());

        // when
        given(dateTimeProvider.getNow()).willReturn(Optional.of(LocalDateTime.now()));
        dividendBatchService.updateUpcomingDividendInfo();

        // then
        assertThat(dividendRepository.count()).isEqualTo(0);
    }

    @Test
    void 새로운_미래_배당금_정보를_생성한다() {
        // given
        Stock stock = stockRepository.save(StockFixture.createStock(AAPL, 12.51, 120000));
        Dividend expected = DividendFixture.createDividend(stock.getId());
        Instant expectedDate = LocalDateTime.now().plusDays(1).toInstant(UTC);

        List<FinancialClient.DividendData> responses = new ArrayList<>();
        responses.add(new FinancialClient.DividendData(
                expectedDate,
                "May 31, 23",
                12.21,
                AAPL,
                12.21,
                expectedDate,
                expectedDate,
                expectedDate));

        given(financialClient.getUpcomingDividendList()).willReturn(responses);

        // when
        dividendBatchService.updateUpcomingDividendInfo();

        // then
        assertThat(dividendRepository.findByStockIdAndExDividendDate(
                stock.getId(),
                expectedDate))
                .isPresent();

        Dividend actual = dividendRepository.findByStockIdAndExDividendDate(
                        stock.getId(),
                        expectedDate)
                .get();

        assertAll(
                () -> assertThat(actual.getDividend()).isEqualTo(expected.getDividend()),
                () -> assertThat(getYear(actual.getExDividendDate())).isEqualTo(getYear(expectedDate)),
                () -> assertThat(getMonth(actual.getExDividendDate())).isEqualTo(getMonth(expectedDate)),
                () -> assertThat(getDayOfMonth(actual.getExDividendDate())).isEqualTo(getDayOfMonth(expectedDate)),
                () -> assertThat(dividendRepository.findAll().size()).isEqualTo(1)
        );
    }
}