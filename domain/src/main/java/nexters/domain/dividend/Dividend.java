package nexters.domain.dividend;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nexters.domain.base.BaseEntity;

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
    private Double dividend;

    @Column(nullable = false, updatable = false)
    private Instant exDividendDate;

    private Instant paymentDate;

    private Instant declarationDate;

    private Dividend(
            UUID stockId,
            Double dividend,
            Instant exDividendDate,
            Instant paymentDate,
            Instant declarationDate) {
        this.stockId = stockId;
        this.dividend = dividend;
        this.exDividendDate = exDividendDate;
        this.paymentDate = paymentDate;
        this.declarationDate = declarationDate;
    }

    /**
     * 배당금 정보를 갱신하는 메서드입니다.
     * @param dividend 갱신할 배당금
     * @param paymentDate 갱신할 배당 지급일
     * @param declarationDate 갱신할 배당 지급 선언일
     */
    public void update(Double dividend, Instant paymentDate, Instant declarationDate) {

        this.dividend = dividend;
        this.paymentDate = paymentDate;
        this.declarationDate = declarationDate;
    }

    public static Dividend createDividend(
            UUID stockId,
            Double dividend,
            Instant exDividendDate,
            Instant paymentDate,
            Instant declarationDate) {
        return new Dividend(stockId, dividend, exDividendDate, paymentDate, declarationDate);
    }
}
