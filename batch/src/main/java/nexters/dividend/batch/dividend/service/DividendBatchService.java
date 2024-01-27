package nexters.dividend.batch.dividend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 배당금 관련 스케쥴러 서비스 클래스입니다.
 *
 * @author Min Ho CHO
 */
@Service
@Transactional
@RequiredArgsConstructor
public class DividendBatchService {

    private final FmpDividendClient fmpDividendClient;

    /**
     * New York 시간대 기준으로 매일 00:00에 배당금 정보를 갱신하는 스케쥴러 메서드입니다.
     */
    @Scheduled(cron = "${schedules.cron.dividend}", zone = "America/New_York")
    public void run() {

        fmpDividendClient.updateDividendData();
    }
}
