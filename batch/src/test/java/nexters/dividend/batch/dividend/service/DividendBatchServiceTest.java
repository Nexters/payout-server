package nexters.dividend.batch.dividend.service;

import nexters.dividend.batch.dividend.dto.FmpDividendResponse;
import nexters.dividend.domain.dividend.Dividend;
import nexters.dividend.domain.dividend.repository.DividendRepository;
import nexters.dividend.domain.stock.Sector;
import nexters.dividend.domain.stock.Stock;
import nexters.dividend.domain.stock.StockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static nexters.dividend.domain.dividend.Dividend.createDividend;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("배당금 스케쥴러 서비스 테스트")
class DividendBatchServiceTest {

    @MockBean
    private FinancialClient financialClient;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private DividendRepository dividendRepository;

    @Autowired
    private DividendBatchService dividendBatchService;

    @Test
    @DisplayName("배당금 스케쥴러 테스트: 새로운 배당금 정보 생성")
    void createDividendTest() {

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

        FmpDividendResponse response = new FmpDividendResponse(
                "2023-12-21",
                "May 31, 23",
                12.21,
                "AAPL",
                12.21,
                "2023-12-21",
                "2023-12-23",
                "2023-12-22");

        List<FmpDividendResponse> responses = new ArrayList<>();
        responses.add(response);

        doReturn(responses).when(financialClient).getDividendData();


        // when
        dividendBatchService.run();

        // then
        assertThat(dividendRepository.findByStockId(stock.getId())).isPresent();
        assertThat(dividendRepository.findByStockId(stock.getId()).get().getDividend()).isEqualTo(dividend.getDividend());
        assertThat(dividendRepository.findByStockId(stock.getId()).get().getExDividendDate()).isEqualTo(dividend.getExDividendDate());
        assertThat(dividendRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("배당금 스케쥴러 테스트: 기존의 배당금 정보 갱신")
    void updateDividendTest() {

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

        FmpDividendResponse response = new FmpDividendResponse(
                "2023-12-21",
                "May 31, 23",
                12.21,
                "AAPL",
                12.21,
                "2023-12-21",
                "2023-12-23",
                "2023-12-22");

        List<FmpDividendResponse> responses = new ArrayList<>();
        responses.add(response);

        doReturn(responses).when(financialClient).getDividendData();

        // when
        dividendBatchService.run();

        // then
        assertThat(dividendRepository.findByStockId(stock.getId())).isPresent();
        assertThat(dividendRepository.findByStockId(stock.getId()).get().getDividend()).isEqualTo(dividend.getDividend());
        assertThat(dividendRepository.findByStockId(stock.getId()).get().getExDividendDate()).isEqualTo(dividend.getExDividendDate());
        assertThat(dividendRepository.findByStockId(stock.getId()).get().getPaymentDate()).isEqualTo(dividend.getPaymentDate());
        assertThat(dividendRepository.findByStockId(stock.getId()).get().getDeclarationDate()).isEqualTo(dividend.getDeclarationDate());
        assertThat(dividendRepository.findAll().size()).isEqualTo(1);
    }
}