package nexters.payout.batch.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import nexters.payout.batch.application.FinancialClient.DividendData;
import nexters.payout.domain.dividend.Dividend;
import nexters.payout.domain.dividend.repository.DividendRepository;
import nexters.payout.domain.stock.Stock;
import nexters.payout.domain.stock.repository.StockRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static nexters.payout.domain.dividend.Dividend.createDividend;

/**
 * 배당금 관련 스케쥴러 서비스 클래스입니다.
 *
 * @author Min Ho CHO
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class DividendBatchService {

    private final StockRepository stockRepository;
    private final DividendRepository dividendRepository;
    private final FinancialClient financialClient;

    /**
     * New York 시간대 기준으로 매일 00:00에 배당금 정보를 갱신하는 스케쥴러 메서드입니다.
     */
    @Scheduled(cron = "${schedules.cron.dividend}", zone = "UTC")
    public void run() {
        List<DividendData> dividendResponses = financialClient.getDividendList();
        for (DividendData dividendData : dividendResponses) {
            stockRepository.findByTicker(dividendData.symbol())
                    .ifPresent(stock -> handleDividendData(stock, dividendData));
        }
    }
    private void handleDividendData(Stock stock, DividendData dividendData) {
        dividendRepository.findByStockIdAndExDividendDate(stock.getId(), parseInstant(dividendData.date()))
                .ifPresentOrElse(
                        existingDividend -> updateDividend(existingDividend, dividendData),
                        () -> createDividend(stock, dividendData));
    }
    private void updateDividend(Dividend existingDividend, DividendData dividendData) {
        existingDividend.update(
                dividendData.dividend(),
                parseInstant(dividendData.paymentDate()),
                parseInstant(dividendData.declarationDate()));
    }
    private void createDividend(Stock stock, DividendData dividendData) {
        Dividend newDividend = Dividend.createDividend(
                stock.getId(),
                dividendData.dividend(),
                parseInstant(dividendData.date()),
                parseInstant(dividendData.paymentDate()),
                parseInstant(dividendData.declarationDate()));
        dividendRepository.save(newDividend);
    }

    /**
     * "yyyy-MM-dd" 형식의 String을 Instant 타입으로 변환하는 메서드입니다.
     *
     * @param date "yyyy-MM-dd" 형식의 String
     * @return 해당하는 Instant 타입
     */
    private Instant parseInstant(String date) {

        if (date == null) return null;
        return Instant.parse(date + "T00:00:00Z");
    }
}
