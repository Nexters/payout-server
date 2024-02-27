package nexters.payout.apiserver.stock.application.dto.response;

import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.dividend.domain.Dividend;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;

public record DividendResponse(
        Double dividendPerShare,
        LocalDate exDividendDate,
        LocalDate paymentDate,
        Double dividendYield,
        List<Month> dividendMonths
) {

    public static DividendResponse noDividend() {
        return new DividendResponse(
                0.0,
                null,
                null,
                0.0,
                Collections.emptyList()
        );
    }

    public static DividendResponse withoutDividendDates(
            final Double dividendPerShare,
            final Double dividendYield,
            final List<Month> dividendMonths
    ) {
        return new DividendResponse(
                dividendPerShare,
                null,
                null,
                dividendYield,
                dividendMonths
        );
    }

    public static DividendResponse fullDividendInfo(
            final Dividend dividend,
            final Double dividendYield,
            final List<Month> dividendMonths
    ) {
        return new DividendResponse(
                dividend.getDividend(),
                InstantProvider.toLocalDate(dividend.getExDividendDate()).withYear(InstantProvider.getThisYear()),
                dividend.getPaymentDate() == null ? null : InstantProvider.toLocalDate(dividend.getPaymentDate())
                        .withYear(InstantProvider.getThisYear()),
                dividendYield,
                dividendMonths
        );
    }
}
