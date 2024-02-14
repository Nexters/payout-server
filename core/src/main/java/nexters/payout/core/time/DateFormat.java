package nexters.payout.core.time;

import java.time.Instant;

public class DateFormat {
    /**
     * "yyyy-MM-dd" 형식의 String을 Instant 타입으로 변환하는 메서드입니다.
     *
     * @param date "yyyy-MM-dd" 형식의 String
     * @return 해당하는 Instant 타입
     */
    public static Instant parseInstant(final String date) {

        if (date == null) return null;
        return Instant.parse(date + "T00:00:00Z");
    }
}
