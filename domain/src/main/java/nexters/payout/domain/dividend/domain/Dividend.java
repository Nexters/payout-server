package nexters.payout.domain.dividend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import nexters.payout.domain.BaseEntity;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
public class Dividend extends BaseEntity {

    @Column(nullable = false, updatable = false)
    private UUID stockId;

    private Double dividend;

    @Column(updatable = false)
    private Instant exDividendDate;

    private Instant paymentDate;

    private Instant declarationDate;

    public Dividend() {
        super(null);
    }

    public Dividend(final UUID id, final UUID stockId, final Double dividend, final Instant exDividendDate,
                    final Instant paymentDate, final Instant declarationDate) {
        super(id);
        this.stockId = stockId;
        this.dividend = dividend;
        this.exDividendDate = exDividendDate;
        this.paymentDate = paymentDate;
        this.declarationDate = declarationDate;
    }

    public Dividend(final UUID stockId, final Double dividend, final Instant exDividendDate,
                    final Instant paymentDate, final Instant declarationDate) {
        this(null, stockId, dividend, exDividendDate, paymentDate, declarationDate);
    }

    public void update(final Double dividend, final Instant paymentDate, final Instant declarationDate) {
        this.dividend = dividend;
        this.paymentDate = paymentDate;
        this.declarationDate = declarationDate;
    }

    public static Dividend create(
            final UUID stockId, final Double dividend, final Instant exDividendDate,
            final Instant paymentDate, final Instant declarationDate) {
        return new Dividend(stockId, dividend, exDividendDate, paymentDate, declarationDate);
    }

    @Override
    public String toString() {
        return "Dividend{" +
                "stockId=" + stockId +
                ", dividend=" + dividend +
                ", exDividendDate=" + exDividendDate +
                ", paymentDate=" + paymentDate +
                ", declarationDate=" + declarationDate +
                '}';
    }
}
