package nexters.payout.domain;

import nexters.payout.domain.dividend.Dividend;

import java.time.Instant;
import java.util.UUID;

public class DividendFixture {
    public static Dividend createDividend(UUID stockId, Double dividend) {
        return new Dividend(
                stockId,
                dividend,
                Instant.now(),
                Instant.now(),
                Instant.now()
        );
    }

    public static Dividend createDividend(UUID stockId, Instant paymentDate) {
        return Dividend.createDividend(
                stockId,
                12.21,
                Instant.parse("2023-12-21T00:00:00Z"),
                paymentDate,
                Instant.parse("2023-12-22T00:00:00Z"));
    }

    public static Dividend createDividend(UUID stockId) {
        return Dividend.createDividend(
                stockId,
                12.21,
                Instant.parse("2023-12-21T00:00:00Z"),
                Instant.parse("2023-12-23T00:00:00Z"),
                Instant.parse("2023-12-22T00:00:00Z"));
    }

    public static Dividend createDividendWithNullDate(UUID stockId) {
        return Dividend.createDividend(
                stockId,
                12.21,
                Instant.parse("2023-12-21T00:00:00Z"),
                null,
                null);
    }
}
