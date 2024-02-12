package nexters.payout.core.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;

import static java.time.ZoneOffset.UTC;

public class InstantTimeProvider {
    public static LocalDate toLocalDate(Instant instant) {
        return LocalDate.ofInstant(instant, UTC);
    }

    public static Integer getThisYear() {
        return getNow().getYear();
    }

    public static Integer getLastYear() {
        return getNow().minusYears(1).getYear();
    }

    private static LocalDate getNow() {
        return LocalDate.ofInstant(Instant.now(), UTC);
    }
}
