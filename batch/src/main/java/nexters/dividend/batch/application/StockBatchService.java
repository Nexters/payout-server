package nexters.dividend.batch.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexters.dividend.batch.application.FinancialClient.LatestStock;
import nexters.dividend.domain.stock.StockRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockBatchService {

    private final FinancialClient financialClient;
    private final StockRepository stockRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    void updateStock() {
        log.info("update stock start..");
        List<LatestStock> stockList = financialClient.getLatestStockList();

        for (LatestStock latestStock : stockList) {
            stockRepository.findByTicker(latestStock.ticker())
                    .ifPresentOrElse(
                            existingStock -> existingStock.update(latestStock.price(), latestStock.volume()),
                            () -> stockRepository.save(latestStock.toDomain())
                    );
        }

        log.info("update stock end..");
    }
}
