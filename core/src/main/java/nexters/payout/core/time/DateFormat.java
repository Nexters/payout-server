package nexters.payout.core.time;

import java.time.Instant;

public class DateFormat {
    /**
     * "yyyy-MM-dd" 형식의 String을 Instant 타입으로 변환합니다.
     */
    public static Instant parseInstant(final String date) {

        if (date == null) return null;
        return Instant.parse(date + "T00:00:00Z");
    }
}
