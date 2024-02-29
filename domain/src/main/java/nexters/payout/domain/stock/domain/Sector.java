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
    TECHNOLOGY("Technology", "TECHNOLOGY"),
    COMMUNICATION_SERVICES("Communication Services", "COMMUNICATION_SERVICES"),
    HEALTHCARE("Healthcare", "HEALTHCARE"),
    CONSUMER_CYCLICAL("Consumer Cyclical", "CONSUMER_CYCLICAL"),
    CONSUMER_DEFENSIVE("Consumer Defensive", "CONSUMER_DEFENSIVE"),
    BASIC_MATERIALS("Basic Materials", "BASIC_MATERIALS"),
    FINANCIAL_SERVICES("Financial Services", "FINANCIAL_SERVICES"),
    INDUSTRIALS("Industrials", "INDUSTRIALS"),
    REAL_ESTATE("Real Estate", "REAL_ESTATE"),
    ENERGY("Energy", "ENERGY"),
    UTILITIES("Utilities", "UTILITIES"),
    INDUSTRIAL_GOODS("Industrial Goods", "INDUSTRIAL_GOODS"),
    FINANCIAL("Financial", "FINANCIAL"),
    SERVICES("Services", "SERVICES"),
    CONGLOMERATES("Conglomerates", "CONGLOMERATES"),
    ETF("ETF", "ETF"),
    ETC("ETC", "ETC");

    private final String name;
    private final String value;

    Sector(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    private static final Map<String, Sector> NAME_TO_SECTOR_MAP = Arrays
            .stream(values())
            .collect(Collectors.toMap(sector -> sector.name, Function.identity()));

    private static final Map<String, Sector> VALUE_TO_SECTOR_MAP = Arrays
            .stream(values())
            .collect(Collectors.toMap(sector -> sector.value, Function.identity()));

    private static final Set<String> ETC_NAMES = Set.of(
            INDUSTRIAL_GOODS.name, FINANCIAL.name, SERVICES.name, CONGLOMERATES.name, ETC.name()
    );

    private static final Set<String> ETC_VALUES = Set.of(
            INDUSTRIAL_GOODS.value, FINANCIAL.value, SERVICES.value, CONGLOMERATES.value, ETC.value
    );

    public static List<String> getNames() {
        return Arrays.stream(Sector.values())
                .map(it -> it.name)
                .filter(name -> !name.isEmpty())
                .toList();
    }

    public static Sector fromName(String sectorName) {
        if (sectorName == null || isEtcCategoryName(sectorName)) {
            return ETC;
        }

        return NAME_TO_SECTOR_MAP.getOrDefault(sectorName, ETC);
    }

    public static Sector fromValue(String sectorValue) {
        if (sectorValue == null || isEtcCategoryValue(sectorValue)) {
            return ETC;
        }

        return VALUE_TO_SECTOR_MAP.getOrDefault(sectorValue, ETC);
    }

    private static boolean isEtcCategoryName(String value) {
        return ETC_NAMES.contains(value);
    }

    private static boolean isEtcCategoryValue(String value) {
        return ETC_VALUES.contains(value);
    }
}
