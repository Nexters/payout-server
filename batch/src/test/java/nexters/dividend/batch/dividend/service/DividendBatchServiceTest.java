package nexters.dividend.batch.dividend.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("배당금 스케쥴러 서비스 테스트")
class DividendBatchServiceTest {

    @SpyBean
    private DividendBatchService dividendBatchService;

    /**
     * 3초 간격으로 실행되는 스케쥴러 메서드에 대해 6초 간 2번 수행되는지에 대해 테스트하는 메서드입니다.
     */
    @Test
    @DisplayName("배당금 스케쥴러 테스트")
    void run() {
        await().atMost(20, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(dividendBatchService, atLeast(2)).run();
        });
    }
}