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
}
