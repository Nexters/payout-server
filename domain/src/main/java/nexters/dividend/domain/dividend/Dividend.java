package nexters.dividend.domain.dividend;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nexters.dividend.domain.BaseEntity;

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

    @Column(nullable = false, updatable = false)
    private UUID stockId;

    @Column(nullable = false)
    private Integer dividend;

    @Column(nullable = false, updatable = false)
    private Instant exDividendDate;

    @Column(nullable = false)
    private Instant paymentDate;

    @Column(nullable = false)
    private Instant declarationDate;

    private Dividend(
            UUID stockId,
            Integer dividend,
            Instant exDividendDate,
            Instant paymentDate,
            Instant declarationDate) {
        this.stockId = stockId;
        this.dividend = dividend;
        this.exDividendDate = exDividendDate;
        this.paymentDate = paymentDate;
        this.declarationDate = declarationDate;
    }

    public static Dividend createDividend(
            UUID stockId,
            Integer dividend,
            Instant exDividendDate,
            Instant paymentDate,
            Instant declarationDate) {
        return new Dividend(stockId, dividend, exDividendDate, paymentDate, declarationDate);
    }
}
