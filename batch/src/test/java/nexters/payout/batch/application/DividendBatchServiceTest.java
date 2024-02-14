package nexters.payout.batch.application;

import nexters.payout.batch.common.AbstractBatchServiceTest;
import nexters.payout.domain.DividendFixture;
import nexters.payout.domain.StockFixture;
import nexters.payout.domain.dividend.Dividend;
import nexters.payout.domain.stock.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doReturn;

@DisplayName("배당금 스케쥴러 서비스 테스트")
class DividendBatchServiceTest extends AbstractBatchServiceTest {

    @Test
    void 새로운_배당금_정보를_생성한다() {

        // given
        Stock stock = stockRepository.save(StockFixture.createStock("AAPL", 12.51, 120000));
        Dividend expected = DividendFixture.createDividend(stock.getId());

        List<FinancialClient.DividendData> responses = new ArrayList<>();
        responses.add(new FinancialClient.DividendData(
                "2023-12-21",
                "May 31, 23",
                12.21,
                "AAPL",
                12.21,
                "2023-12-21",
                "2023-12-23",
                "2023-12-22"));

        doReturn(responses).when(financialClient).getDividendList();

        // when
        dividendBatchService.run();

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
    void 기존의_배당금_정보를_갱신한다() {

        // given
        Stock stock = stockRepository.save(StockFixture.createStock("AAPL", 12.51, 120000));
        Dividend expected = dividendRepository.save(DividendFixture.createDividendWithNullDate(stock.getId()));

        List<FinancialClient.DividendData> responses = new ArrayList<>();
        responses.add(new FinancialClient.DividendData(
                "2023-12-21",
                "May 31, 23",
                12.21,
                "AAPL",
                12.21,
                "2023-12-21",
                "2023-12-23",
                "2023-12-22"));

        doReturn(responses).when(financialClient).getDividendList();

        // when
        dividendBatchService.run();

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
}