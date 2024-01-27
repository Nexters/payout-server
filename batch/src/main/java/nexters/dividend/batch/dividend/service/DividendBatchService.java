package nexters.dividend.batch.dividend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nexters.dividend.batch.dividend.dto.FmpDividendResponse;
import nexters.dividend.domain.dividend.Dividend;
import nexters.dividend.domain.dividend.repository.DividendRepository;
import nexters.dividend.domain.stock.Stock;
import nexters.dividend.domain.stock.StockRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
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
@RequiredArgsConstructor
public class DividendBatchService {

    @Value("${fmp.key}")
    private String FMP_API_KEY;
    @Value("${fmp.base-url}")
    private String FMP_API_BASE_URL;
    @Value("${fmp.stock-dividend-calendar-postfix}")
    private String FMP_API_STOCK_DIVIDEND_CALENDAR_POSTFIX;

    private final DividendRepository dividendRepository;
    private final StockRepository stockRepository;

    /**
     * New York 시간대 기준으로 매일 00:00에 배당금 정보를 갱신하는 스케쥴러 메서드입니다.
     */
    @Scheduled(cron = "${schedules.cron.dividend}", zone = "America/New_York")
    public void run() {
        WebClient client =
                WebClient
                        .builder()
                        .baseUrl(FMP_API_BASE_URL)
                        .build();

        // 3개월 간 총 4번의 데이터를 조회함으로써 기준 날짜로부터 이전 1년 간의 데이터를 조회
        for (int i = 0; i < 4; i++) {

            Instant date = ZonedDateTime.now(ZoneOffset.UTC).minusDays(1).minusMonths(i).toInstant();
            List<FmpDividendResponse> dividendResponses = client.get()
                    .uri(uriBuilder ->
                            uriBuilder
                                    .path(FMP_API_STOCK_DIVIDEND_CALENDAR_POSTFIX)
                                    .queryParam("to", formatInstant(date))
                                    .queryParam("apikey", FMP_API_KEY)
                                    .build())
                    .retrieve()
                    .bodyToFlux(FmpDividendResponse.class)
                    .collectList()
                    .block();

            if (dividendResponses == null) {

                // TODO: FMP API가 null을 return한 경우 exception을 throw하도록 변경
                continue;
            }

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
    }

    /**
     * Instant를 yyyy-MM-dd 형식의 String으로 변환하는 메서드입니다.
     * @param instant instant 데이터
     * @return 날짜 String 데이터
     */
    private String formatInstant(Instant instant) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(Date.from(instant));
    }

    /**
     * "yyyy-MM-dd" 형식의 String을 Instant 타입으로 변환하는 메서드입니다.
     * @param date "yyyy-MM-dd" 형식의 String
     * @return 해당하는 Instant 타입
     */
    private Instant parseInstant(String date) {

        if (date == null) return null;
        return Instant.parse(date + "T00:00:00Z");
    }
}
