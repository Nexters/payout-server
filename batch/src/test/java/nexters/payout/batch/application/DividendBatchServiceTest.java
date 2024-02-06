package nexters.payout.batch.application;

import nexters.payout.batch.common.AbstractBatchServiceTest;
import nexters.payout.domain.dividend.Dividend;
import nexters.payout.domain.stock.Sector;
import nexters.payout.domain.stock.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static nexters.payout.domain.dividend.Dividend.createDividend;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@DisplayName("배당금 스케쥴러 서비스 테스트")
class DividendBatchServiceTest extends AbstractBatchServiceTest {

    @Test
    @DisplayName("새로운 배당금 정보를 생성한다")
    void 새로운_배당금_정보를_생성한다() {

        // given
        Stock stock = stockRepository.save(
                new Stock(
                        "AAPL",
                        "Apple",
                        Sector.TECHNOLOGY,
                        "NYSE",
                        "ETC",
                        12.51,
                        120000));

        Dividend dividend = createDividend(
                stock.getId(),
                12.21,
                Instant.parse("2023-12-21T00:00:00Z"),
                Instant.parse("2023-12-23T00:00:00Z"),
                Instant.parse("2023-12-22T00:00:00Z"));

        FinancialClient.DividendData response = new FinancialClient.DividendData(
                "2023-12-21",
                "May 31, 23",
                12.21,
                "AAPL",
                12.21,
                "2023-12-21",
                "2023-12-23",
                "2023-12-22");

        List<FinancialClient.DividendData> responses = new ArrayList<>();
        responses.add(response);

        doReturn(responses).when(financialClient).getDividendList();


        // when
        dividendBatchService.run();

        // then
        assertThat(dividendRepository.findByStockId(stock.getId())).isPresent();
        assertThat(dividendRepository.findByStockId(stock.getId()).get().getDividend()).isEqualTo(dividend.getDividend());
        assertThat(dividendRepository.findByStockId(stock.getId()).get().getExDividendDate()).isEqualTo(dividend.getExDividendDate());
        assertThat(dividendRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("기존의 배당금 정보를 갱신한다.")
    void 기존의_배당금_정보를_갱신한다() {

        // given
        Stock stock = stockRepository.save(
                new Stock(
                        "AAPL",
                        "Apple",
                        Sector.TECHNOLOGY,
                        "NYSE",
                        "ETC",
                        12.51,
                        120000));

        Dividend dividend = dividendRepository.save(createDividend(
                stock.getId(),
                12.21,
                Instant.parse("2023-12-21T00:00:00Z"),
                null,
                null));

        FinancialClient.DividendData response = new FinancialClient.DividendData(
                "2023-12-21",
                "May 31, 23",
                12.21,
                "AAPL",
                12.21,
                "2023-12-21",
                "2023-12-23",
                "2023-12-22");

        List<FinancialClient.DividendData> responses = new ArrayList<>();
        responses.add(response);

        doReturn(responses).when(financialClient).getDividendList();

        // when
        dividendBatchService.run();

        // then
        assertThat(dividendRepository.findByStockId(stock.getId())).isPresent();
        assertThat(dividendRepository.findByStockId(stock.getId()).get().getDividend()).isEqualTo(dividend.getDividend());
        assertThat(dividendRepository.findByStockId(stock.getId()).get().getExDividendDate()).isEqualTo(dividend.getExDividendDate());
        assertThat(dividendRepository.findByStockId(stock.getId()).get().getPaymentDate()).isEqualTo(Instant.parse("2023-12-23T00:00:00Z"));
        assertThat(dividendRepository.findByStockId(stock.getId()).get().getDeclarationDate()).isEqualTo(Instant.parse("2023-12-22T00:00:00Z"));
        assertThat(dividendRepository.findAll().size()).isEqualTo(1);
    }
}