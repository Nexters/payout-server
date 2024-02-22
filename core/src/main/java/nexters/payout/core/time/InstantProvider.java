package nexters.payout.core.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static java.time.ZoneOffset.UTC;

public class InstantProvider {
    public static LocalDate toLocalDate(Instant instant) {
        return LocalDate.ofInstant(instant, UTC);
    }

    public static Integer getThisYear() {
        return getNow().getYear();
    }

    public static Integer getNextYear() {
        return getNow().plusYears(1).getYear();
    }

    public static Integer getLastYear() {
        return getNow().minusYears(1).getYear();
    }

    public static Instant getYesterday() {
        return getNow().minusDays(1).atStartOfDay(ZoneId.of("UTC")).toInstant();
    }

    private static LocalDate getNow() {
        return LocalDate.ofInstant(Instant.now(), UTC);
    }
}
