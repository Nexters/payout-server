package nexters.payout.core.time;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.ZoneOffset.UTC;

public class InstantProvider {
    public static LocalDate toLocalDate(Instant instant) {
        return LocalDate.ofInstant(instant, UTC);
    }

    public static List<YearMonth> generateNext12Months() {
        YearMonth startYearMonth = getThisYearMonth();
        YearMonth endYearMonth = getAfterYearMonth(11);

        return Stream.iterate(startYearMonth, date -> date.plusMonths(1))
                .limit(startYearMonth.until(endYearMonth, ChronoUnit.MONTHS) + 1)
                .collect(Collectors.toList());
    }

    public static YearMonth getThisYearMonth() {
        return YearMonth.of(getNow().getYear(), getNow().getMonth());
    }

    public static YearMonth getAfterYearMonth(int month) {
        return YearMonth.of(getNow().plusMonths(month).getYear(), getNow().plusMonths(month).getMonthValue());
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

    public static Integer getYear(Instant date) {
        return ZonedDateTime.ofInstant(date, UTC).getYear();
    }

    public static Integer getMonth(Instant date) {
        return ZonedDateTime.ofInstant(date, UTC).getMonthValue();
    }

    public static Integer getDayOfMonth(Instant date) {
        return ZonedDateTime.ofInstant(date, UTC).getDayOfMonth();
    }

    public static LocalDate getNow() {
        return LocalDate.ofInstant(Instant.now(), UTC);
    }
}
