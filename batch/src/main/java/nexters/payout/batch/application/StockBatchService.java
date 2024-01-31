package nexters.payout.batch.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexters.payout.domain.stock.repository.StockRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockBatchService {

    private final FinancialClient financialClient;
    private final StockRepository stockRepository;

    /**
     * UTC 기준 매일 자정 모든 종목의 현재가와 거래량을 업데이트합니다.
     */
    @Transactional
    @Scheduled(fixedDelay = 1000000, zone = "UTC")
    void run() {
        log.info("update stock start..");
        List<FinancialClient.StockData> stockList = financialClient.getLatestStockList();

        for (FinancialClient.StockData stockData : stockList) {
            stockRepository.findByTicker(stockData.ticker())
                    .ifPresentOrElse(
                            existingStock -> existingStock.update(stockData.price(), stockData.volume()),
                            () -> stockRepository.save(stockData.toDomain())
                    );
        }
        log.info("update stock end..");
    }
}
