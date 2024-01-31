package nexters.payout.domain.stock;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Exchange {
    NASDAQ,
    NYSE,
    AMEX;

    public static List<String> getNames() {
        return Arrays.stream(Exchange.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
