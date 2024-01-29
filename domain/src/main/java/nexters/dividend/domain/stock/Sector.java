package nexters.dividend.domain.stock;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
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
    ETC("");

    private final String value;

    Sector(final String value) {
        this.value = value;
    }

    public static List<String> getNames() {
        return Arrays.stream(Sector.values())
                .map(it -> it.value)
                .toList();
    }

    public static Sector fromValue(String value) {
        for (Sector sector : Sector.values()) {
            if (sector.getValue().equalsIgnoreCase(value)) {
                return sector;
            }
        }
        return ETC;
    }
}
