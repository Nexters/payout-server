package nexters.payout.batch.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexters.payout.batch.application.client.FinancialClient;
import nexters.payout.batch.application.client.FinancialClient.StockData;
import nexters.payout.batch.application.client.StockLogo;
import nexters.payout.domain.stock.application.StockCommandService;
import nexters.payout.domain.stock.domain.repository.StockRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockBatchService {

    private final FinancialClient financialClient;
    private final StockCommandService stockCommandService;
    private final StockLogo stockLogo;
    private final StockRepository stockRepository;

    /**
     * UTC 시간대 기준 매일 자정에 모든 종목의 현재가와 거래량을 업데이트합니다.
     */
    @Scheduled(cron = "${schedules.cron.stock}", zone = "UTC")
    void updateStock() {
        log.info("update stock start..");
        List<StockData> stockList = financialClient.getLatestStockList();

        for (StockData stockData : stockList) {
            try {
                stockRepository.findByTicker(stockData.ticker()).ifPresentOrElse(
                        existing -> stockCommandService.update(stockData.ticker(), stockData.toDomain()),
                        () -> stockCommandService.create(stockData.toDomain(stockLogo.getLogoUrl(stockData.ticker())))
                );
            } catch (Exception e) {
                log.error("fail to save(update) stock: " + stockData);
                log.error(e.getMessage());
            }
        }

        log.info("update stock end..");
    }
}
