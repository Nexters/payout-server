package nexters.payout.batch.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexters.payout.domain.stock.repository.StockRepository;
import nexters.payout.batch.application.FinancialClient.StockData;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockBatchService {

    private final FinancialClient financialClient;
    private final StockRepository stockRepository;

    /**
     * UTC 시간대 기준 매일 자정 모든 종목의 현재가와 거래량을 업데이트합니다.
     */
    @Transactional
    @Scheduled(cron = "${schedules.cron.stock}", zone = "UTC")
    void run() {
        log.info("update stock start..");
        List<StockData> stockList = financialClient.getLatestStockList();

        for (StockData stockData : stockList) {
            stockRepository.findByTicker(stockData.ticker())
                    .ifPresentOrElse(
                            existingStock -> existingStock.update(stockData.price(), stockData.volume()),
                            () -> saveNewStock(stockData)
                    );
        }
        log.info("update stock end..");
    }

    private void saveNewStock(final StockData stockData) {
        try {
            stockRepository.save(stockData.toDomain());
        } catch (Exception e) {
            log.error("fail to save stock: " + stockData);
            log.error(e.getMessage());
        }
    }
}
