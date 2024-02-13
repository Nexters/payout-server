package nexters.payout.domain.stock;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

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
    ETC("");

    private final String name;

    Sector(final String name) {
        this.name = name;
    }

    public static List<String> getNames() {
        return Arrays.stream(Sector.values())
                .map(it -> it.name)
                .toList();
    }

    public static Sector fromValue(String value) {
        for (Sector sector : Sector.values()) {
            if (sector.getName().equalsIgnoreCase(value)) {
                return sector;
            }
        }
        return ETC;
    }
}
