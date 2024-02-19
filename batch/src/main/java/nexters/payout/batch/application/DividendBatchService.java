package nexters.payout.batch.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import nexters.payout.batch.application.FinancialClient.DividendData;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.dividend.application.DividendCommandService;
import nexters.payout.domain.dividend.application.dto.UpdateDividendRequest;
import nexters.payout.domain.dividend.domain.repository.DividendRepository;
import nexters.payout.domain.stock.domain.Stock;
import nexters.payout.domain.stock.domain.repository.StockRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 배당금 관련 스케쥴러 서비스 클래스입니다.
 *
 * @author Min Ho CHO
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DividendBatchService {

    private final FinancialClient financialClient;
    private final DividendCommandService dividendCommandService;
    private final StockRepository stockRepository;

    /**
     * UTC 시간대 기준으로 매일 00:00에 배당금 정보를 갱신합니다.
     */
    @Scheduled(cron = "${schedules.cron.dividend}", zone = "UTC")
    public void run() {
        List<DividendData> dividendResponses = financialClient.getDividendList();
        for (DividendData dividendData : dividendResponses) {
            stockRepository.findByTicker(dividendData.symbol())
                    .ifPresent(stock -> handleDividendData(stock, dividendData));
        }
    }

    public void handleDividendData(final Stock stock, final DividendData dividendData) {
        try {
            dividendCommandService.saveOrUpdate(
                    stock.getId(),
                    Dividend.create(
                            stock.getId(), dividendData.dividend(), dividendData.exDividendDate(),
                            dividendData.paymentDate(), dividendData.declarationDate()
                    )
            );
        } catch (Exception e) {
            log.error("fail to save(update) dividend: " + dividendData);
            log.error(e.getMessage());
        }
    }
}
