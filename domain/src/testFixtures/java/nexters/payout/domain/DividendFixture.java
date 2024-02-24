package nexters.payout.domain;

import nexters.payout.domain.dividend.domain.Dividend;

import java.time.Instant;
import java.util.UUID;

public class DividendFixture {
    public static Dividend createDividend(UUID stockId, Double dividend) {
        return new Dividend(
                UUID.randomUUID(),
                stockId,
                dividend,
                Instant.now(),
                Instant.now(),
                Instant.now()
        );
    }

    public static Dividend createDividend(UUID stockId, Instant paymentDate) {
        return new Dividend(
                UUID.randomUUID(),
                stockId,
                12.21,
                Instant.parse("2023-12-21T00:00:00Z"),
                paymentDate,
                Instant.parse("2023-12-22T00:00:00Z"));
    }

    public static Dividend createDividend(UUID stockId, Double dividend, Instant paymentDate) {
        return new Dividend(
                UUID.randomUUID(),
                stockId,
                dividend,
                Instant.parse("2023-12-21T00:00:00Z"),
                paymentDate,
                Instant.parse("2023-12-22T00:00:00Z"));
    }

    public static Dividend createDividendWithExDividendDate(UUID stockId, Double dividend, Instant exDividendDate) {
        return new Dividend(
                UUID.randomUUID(),
                stockId,
                dividend,
                exDividendDate,
                exDividendDate,
                exDividendDate);
    }

    public static Dividend createDividend(UUID stockId) {
        return new Dividend(
                UUID.randomUUID(),
                stockId,
                12.21,
                Instant.parse("2023-12-21T00:00:00Z"),
                Instant.parse("2023-12-23T00:00:00Z"),
                Instant.parse("2023-12-22T00:00:00Z"));
    }

    public static Dividend createDividendWithNullDate(UUID stockId) {
        return new Dividend(
                UUID.randomUUID(),
                stockId,
                12.21,
                Instant.parse("2023-12-21T00:00:00Z"),
                null,
                null);
    }
}
