package nexters.domain.stock;

public enum Sector {
    TECHNOLOGY("Technology"),
    COMMUNICATION_SERVICES("CommunicationServices"),
    HEALTHCARE("Healthcare"),
    CONSUMER_CYCLICAL("ConsumerCyclical"),
    CONSUMER_DEFENSIVE("ConsumerDefensive"),
    BASIC_MATERIALS("BasicMaterials"),
    FINANCIAL_SERVICES("FinancialServices"),
    INDUSTRIALS("Industrials"),
    REAL_ESTATE("RealEstate"),
    ENERGY("Energy"),
    UTILITIES("Utilities"),
    ETC("ETC");

    private final String value;

    Sector(final String value) {
        this.value = value;
    }
}
