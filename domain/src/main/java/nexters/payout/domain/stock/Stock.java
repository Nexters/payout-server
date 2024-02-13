package nexters.payout.domain.stock;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nexters.payout.domain.BaseEntity;
import nexters.payout.domain.dividend.Dividend;
import nexters.payout.domain.stock.service.DividendAnalysisService;

import java.time.Month;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false, updatable = false)
    private UUID id;

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

    public Stock(final UUID id, final String ticker, final String name,
                 final Sector sector, final String exchange, final String industry,
                 final Double price, final Integer volume) {
        validateTicker(ticker);
        this.id = id;
        this.ticker = ticker;
        this.name = name;
        this.sector = sector;
        this.exchange = exchange;
        this.industry = industry;
        this.price = price;
        this.volume = volume;
    }

    public Stock(final String ticker, final String name,
                 final Sector sector, final String exchange, final String industry,
                 final Double price, final Integer volume) {
        this(null, ticker, name, sector, exchange, industry, price, volume);
    }

    private void validateTicker(final String ticker) {
        if (ticker.isBlank()) {
            throw new IllegalArgumentException("ticker must not be null or empty");
        }
    }

    public void update(
            final Double price,
            final Integer volume) {
        this.price = price;
        this.volume = volume;
    }

    public Double calculateDividendYield(final Dividend dividend) {
        if (this.price == null || this.price == 0) {
            return 0.0;
        }
        return dividend.getDividend() / this.price;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Stock && this.id.equals(((Stock) obj).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
