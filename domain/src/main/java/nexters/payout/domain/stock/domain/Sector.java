package nexters.payout.domain.stock.domain;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum Sector {
    TECHNOLOGY("Technology"),
    COMMUNICATION_SERVICES("Communication Services"),
    HEALTHCARE("Healthcare"),
    CONSUMER_CYCLICAL("Consumer Cyclical"),
    CONSUMER_DEFENSIVE("Consumer Defensive"),
    BASIC_MATERIALS("Basic Materials"),
    FINANCIAL_SERVICES("Financial Services"),
    INDUSTRIALS("Industrials"),
    REAL_ESTATE("Real Estate"),
    ENERGY("Energy"),
    UTILITIES("Utilities"),
    INDUSTRIAL_GOODS("Industrial Goods"),
    FINANCIAL("Financial"),
    SERVICES("Services"),
    CONGLOMERATES("Conglomerates"),
    ETF("ETF"),
    ETC("ETC");

    private final String name;

    Sector(final String name) {
        this.name = name;
    }

    private static final Map<String, Sector> NAME_TO_SECTOR_MAP = Arrays
            .stream(values())
            .collect(Collectors.toMap(sector -> sector.name, Function.identity()));

    private static final Set<String> ETC_NAMES = Set.of(
            INDUSTRIAL_GOODS.name, FINANCIAL.name, SERVICES.name, CONGLOMERATES.name, ETC.name()
    );

    public static List<String> getNames() {
        return Arrays.stream(Sector.values())
                .map(it -> it.name)
                .filter(name -> !name.isEmpty())
                .toList();
    }

    public static Sector fromValue(String sectorName) {
        if (sectorName == null || isEtcCategory(sectorName)) {
            return ETC;
        }

        return NAME_TO_SECTOR_MAP.getOrDefault(sectorName, ETC);
    }

    private static boolean isEtcCategory(String value) {
        return ETC_NAMES.contains(value);
    }
}
