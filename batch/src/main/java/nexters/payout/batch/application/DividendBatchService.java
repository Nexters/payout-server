package nexters.payout.batch.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import nexters.payout.batch.application.FinancialClient.DividendData;
import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.dividend.application.DividendCommandService;
import nexters.payout.domain.stock.domain.Stock;
import nexters.payout.domain.stock.domain.repository.StockRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * UTC 시간대 기준으로 매주 월요일 새벽 4시에 작년 한해 동안의 배당금 정보를 갱신합니다.
     */
    @Scheduled(cron = "${schedules.cron.dividend.past}", zone = "UTC")
    public void updatePastDividendInfo() {
        handleDividendData(financialClient.getPastDividendList());
    }

    /**
     * 어제 삽입된 미래 배당금 정보를 삭제하고, UTC 시간대 기준으로 매일 새벽 4시에 현재 날짜로부터 3개월 간의 다가오는 배당금 정보를 갱신합니다.
     */
    @Scheduled(cron = "${schedules.cron.dividend.future}", zone = "UTC")
    public void updateIncomingDividendInfo() {
        dividendCommandService.deleteInvalidDividend();
        handleDividendData(financialClient.getIncomingDividendList());
    }

    private void saveOrUpdateDividendData(final Stock stock, final DividendData dividendData) {
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

    private void handleDividendData(List<DividendData> dividendResponses) {
        for (DividendData dividendData : dividendResponses) {
            stockRepository.findByTicker(dividendData.symbol())
                    .ifPresent(stock -> saveOrUpdateDividendData(stock, dividendData));
        }
    }
}
