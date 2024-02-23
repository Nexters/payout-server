package nexters.payout.apiserver.stock.application.dto.response;

import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.stock.domain.Stock;

import java.time.Instant;
import java.util.UUID;

public record UpcomingDividendResponse(
        UUID stockId,
        String ticker,
        String logoUrl,
        Instant exDividendDate
) {
    public static UpcomingDividendResponse of(Stock stock, Dividend dividend) {
        return new UpcomingDividendResponse(
                stock.getId(),
                stock.getTicker(),
                stock.getLogoUrl(),
                dividend.getExDividendDate()
        );
    }
}
