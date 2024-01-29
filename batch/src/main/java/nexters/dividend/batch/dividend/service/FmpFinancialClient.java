package nexters.dividend.batch.dividend.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import nexters.dividend.batch.dividend.dto.FmpDividendResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * FMP API Client 관련 구현체 클래스입니다.
 *
 * @author Min Ho CHO
 */
@Service
@Slf4j
@Transactional
public class FmpFinancialClient implements FinancialClient {

    @Value("${financial.fmp.key}")
    private String FMP_API_KEY;
    @Value("${financial.fmp.base-url}")
    private String FMP_API_BASE_URL;
    @Value("${financial.fmp.stock-dividend-calendar-postfix}")
    private String FMP_API_STOCK_DIVIDEND_CALENDAR_POSTFIX;

    /**
     * 배당금 관련 정보를 업데이트하는 메서드입니다.
     */
    @Override
    public List<FmpDividendResponse> getDividendData() {

        WebClient client =
                WebClient
                        .builder()
                        .baseUrl(FMP_API_BASE_URL)
                        .build();

        // 3개월 간 총 4번의 데이터를 조회함으로써 기준 날짜로부터 이전 1년 간의 데이터를 조회
        List<FmpDividendResponse> result = new ArrayList<>();
        for (int i = 0; i < 4; i++) {

            Instant date = ZonedDateTime.now(ZoneOffset.UTC).minusDays(1).minusMonths(i).toInstant();

            List<FmpDividendResponse> dividendResponses =
                    client.get()
                            .uri(uriBuilder ->
                                    uriBuilder
                                            .path(FMP_API_STOCK_DIVIDEND_CALENDAR_POSTFIX)
                                            .queryParam("to", formatInstant(date))
                                            .queryParam("apikey", FMP_API_KEY)
                                            .build())
                            .retrieve()
                            .bodyToFlux(FmpDividendResponse.class)
                            .onErrorResume(throwable -> {
                                log.error("FmpClient updateDividendData 수행 중 에러 발생: {}", throwable.getMessage());
                                return Mono.empty();
                            })
                            .collectList()
                            .block();

            if (dividendResponses == null) {

                log.error("FmpClient updateDividendData 수행 중 에러 발생: dividendResponses is null");
                continue;
            }

            result.addAll(dividendResponses);
        }

        return result;
    }

    /**
     * Instant를 yyyy-MM-dd 형식의 String으로 변환하는 메서드입니다.
     *
     * @param instant instant 데이터
     * @return 날짜 String 데이터
     */
    private String formatInstant(Instant instant) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(Date.from(instant));
    }
}
