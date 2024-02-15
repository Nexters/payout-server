package nexters.payout.batch.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexters.payout.batch.application.FinancialClient.StockData;
import nexters.payout.domain.stock.application.StockCommandService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockBatchService {

    private final FinancialClient financialClient;
    private final StockCommandService stockCommandService;

    /**
     * UTC 시간대 기준 매일 자정 모든 종목의 현재가와 거래량을 업데이트합니다.
     */
    @Scheduled(cron = "${schedules.cron.stock}", zone = "UTC")
    void run() {
        log.info("update stock start..");
        List<StockData> stockList = financialClient.getLatestStockList();

        for (StockData stockData : stockList) {
            try {
                stockCommandService.saveOrUpdate(stockData.ticker(), stockData.toDomain());
            } catch (Exception e) {
                log.error("fail to save(update) stock: " + stockData);
                log.error(e.getMessage());
            }
        }

        log.info("update stock end..");
    }
}
