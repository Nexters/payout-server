package nexters.payout.batch.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexters.payout.core.exception.error.NotFoundException;
import nexters.payout.domain.stock.application.StockCommandService;
import nexters.payout.domain.stock.application.dto.UpdateStockRequest;
import nexters.payout.domain.stock.domain.Stock;
import nexters.payout.domain.stock.domain.repository.StockRepository;
import nexters.payout.batch.application.FinancialClient.StockData;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockBatchService {

    private final FinancialClient financialClient;
    private final StockRepository stockRepository;
    private final StockCommandService stockCommandService;

    /**
     * UTC 시간대 기준 매일 자정 모든 종목의 현재가와 거래량을 업데이트합니다.
     */
    @Scheduled(cron = "${schedules.cron.stock}", zone = "UTC")
    void run() {
        log.info("update stock start..");
        List<StockData> stockList = financialClient.getLatestStockList();

        for (StockData stockData : stockList) {
            stockRepository.findByTicker(stockData.ticker())
                    .ifPresentOrElse(
                            existing -> update(existing.getTicker(), stockData),
                            () -> create(stockData)
                    );
        }
        log.info("update stock end..");
    }

    private void create(final StockData stockData) {
        try {
            stockCommandService.save(stockData.toDomain());
        } catch (Exception e) {
            log.error("fail to save stock: " + stockData);
            log.error(e.getMessage());
        }
    }

    private void update(final String ticker, final StockData stockData) {
        stockCommandService.update(ticker, new UpdateStockRequest(stockData.price(), stockData.volume()));
    }
}
