package nexters.payout.domain.dividend.application.dto;

import java.time.Instant;

public record UpdateDividendRequest(
        Double dividend,
        Instant paymentDate,
        Instant declarationDate
) {
}
