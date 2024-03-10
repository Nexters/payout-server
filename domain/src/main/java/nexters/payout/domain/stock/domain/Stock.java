package nexters.payout.domain.stock.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nexters.payout.domain.BaseEntity;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
public class Stock extends BaseEntity {

    @Column(unique = true, nullable = false, length = 50)
    private String ticker;

    private String name;

    @Enumerated(EnumType.STRING)
    private Sector sector;

    @Column(length = 10)
    private String exchange;

    private String industry;

    private Double price;

    private Integer volume;

    private String logoUrl;

    public Stock() {super(null);}

    public Stock(final UUID id, final String ticker, final String name,
                 final Sector sector, final String exchange, final String industry,
                 final Double price, final Integer volume, final String logoUrl) {
        super(id);
        validateTicker(ticker);
        this.ticker = ticker;
        this.name = name;
        this.sector = sector;
        this.exchange = exchange;
        this.industry = industry;
        this.price = price;
        this.volume = volume;
        this.logoUrl = logoUrl;
    }

    public Stock(final String ticker, final String name,
                 final Sector sector, final String exchange, final String industry,
                 final Double price, final Integer volume, final String logoUrl) {
        this(null, ticker, name, sector, exchange, industry, price, volume, logoUrl);
    }

    private void validateTicker(final String ticker) {
        if (ticker.isBlank()) {
            throw new IllegalArgumentException("ticker must not be null or empty");
        }
    }

    public void update(
            final Double price,
            final Integer volume,
            final Sector sector) {
        this.price = price;
        this.volume = volume;
        this.sector = sector;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "ticker='" + ticker + '\'' +
                ", name='" + name + '\'' +
                ", sector=" + sector +
                ", exchange='" + exchange + '\'' +
                ", industry='" + industry + '\'' +
                ", price=" + price +
                ", volume=" + volume +
                '}';
    }
}
