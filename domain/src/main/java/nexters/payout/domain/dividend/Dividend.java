package nexters.payout.domain.dividend;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nexters.payout.domain.BaseEntity;

import java.time.Instant;
import java.util.UUID;

/**
 * 배당금을 표현하는 클래스입니다.
 *
 * @author Min Ho CHO
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dividend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID stockId;

    @Column(nullable = false)
    private Double dividend;

    @Column(nullable = false, updatable = false)
    private Instant exDividendDate;

    private Instant paymentDate;

    private Instant declarationDate;

    public Dividend(UUID id, UUID stockId, Double dividend, Instant exDividendDate,
                    Instant paymentDate, Instant declarationDate) {
        this.id = id;
        this.stockId = stockId;
        this.dividend = dividend;
        this.exDividendDate = exDividendDate;
        this.paymentDate = paymentDate;
        this.declarationDate = declarationDate;
    }

    public Dividend(UUID stockId, Double dividend, Instant exDividendDate,
                    Instant paymentDate, Instant declarationDate) {
        this(null, stockId, dividend, exDividendDate, paymentDate, declarationDate);
    }

    public void update(Double dividend, Instant paymentDate, Instant declarationDate) {
        this.dividend = dividend;
        this.paymentDate = paymentDate;
        this.declarationDate = declarationDate;
    }

    public static Dividend create(
            UUID stockId, Double dividend, Instant exDividendDate,
            Instant paymentDate, Instant declarationDate) {
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
