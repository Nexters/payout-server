package nexters.payout.domain.dividend.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import nexters.payout.domain.dividend.domain.Dividend;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static nexters.payout.domain.dividend.domain.QDividend.dividend1;
import static nexters.payout.domain.stock.domain.QStock.stock;


/**
 * Dividend 엔티티 관련 custom query repository 클래스입니다.
 *
 * @author Min Ho CHO
 */
public class DividendRepositoryImpl implements DividendRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public DividendRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<Dividend> findByTickerAndExDividendDate(String ticker, Instant exDividendDate) {

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(dividend1)
                        .join(stock).on(dividend1.stockId.eq(stock.id))
                        .where(stock.ticker.eq(ticker).and(dividend1.exDividendDate.eq(exDividendDate)))
                        .fetchOne()
        );
    }

    @Override
    public List<Dividend> findAllByTickerAndYearAndMonth(String ticker, int year, int month) {

        return queryFactory
                .selectFrom(dividend1)
                .join(stock).on(dividend1.stockId.eq(stock.id))
                .where(dividend1.exDividendDate.year().eq(year).and(dividend1.exDividendDate.month().eq(month)))
                .fetch();
    }
}
