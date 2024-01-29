package nexters.dividend.batch.dividend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexters.dividend.batch.dividend.dto.FmpDividendResponse;
import nexters.dividend.domain.dividend.Dividend;
import nexters.dividend.domain.dividend.repository.DividendRepository;
import nexters.dividend.domain.stock.Stock;
import nexters.dividend.domain.stock.StockRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static nexters.dividend.domain.dividend.Dividend.createDividend;

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
    @Scheduled(cron = "${schedules.cron.dividend}", zone = "America/New_York")
    public void run() {

        List<FmpDividendResponse> dividendResponses = financialClient.getDividendData();

        for (FmpDividendResponse response : dividendResponses) {

            Optional<Stock> findStock = stockRepository.findByTicker(response.getSymbol());
            if (findStock.isEmpty()) continue;  // NYSE, NASDAQ, AMEX 이외의 주식인 경우 continue

            Optional<Dividend> findDividend = dividendRepository.findByStockId(findStock.get().getId());
            if (findDividend.isPresent()) {
                // 기존의 Dividend 엔티티가 존재할 경우 정보 갱신
                findDividend.get().update(
                        response.getDividend(),
                        parseInstant(response.getPaymentDate()),
                        parseInstant(response.getDeclarationDate()));
            } else {
                // 기존의 Dividend 엔티티가 존재하지 않을 경우 새로 생성
                dividendRepository.save(createDividend(
                        findStock.get().getId(),
                        response.getDividend(),
                        parseInstant(response.getDate()),
                        parseInstant(response.getPaymentDate()),
                        parseInstant(response.getDeclarationDate())));
            }
        }
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
