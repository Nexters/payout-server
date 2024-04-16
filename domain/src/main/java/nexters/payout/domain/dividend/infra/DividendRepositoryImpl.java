package nexters.payout.domain.dividend.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.dividend.domain.Dividend;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static nexters.payout.domain.dividend.domain.QDividend.dividend1;
import static nexters.payout.domain.stock.domain.QStock.stock;

@Repository
public class DividendRepositoryImpl implements DividendRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public DividendRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<Dividend> findByStockIdAndExDividendDate(UUID stockId, Instant date) {

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(dividend1)
                        .innerJoin(stock).on(dividend1.stockId.eq(stock.id))
                        .where(stock.id.eq(stockId)
                                .and(dividend1.exDividendDate.year().eq(InstantProvider.getYear(date)))
                                .and(dividend1.exDividendDate.month().eq(InstantProvider.getMonth(date)))
                                .and(dividend1.exDividendDate.dayOfMonth().eq(InstantProvider.getDayOfMonth(date))))
                        .fetchOne()
        );
    }

    @Override
    public List<Dividend> findAllByTickerAndYearAndMonth(String ticker, Integer year, Integer month) {

        return queryFactory
                .selectFrom(dividend1)
                .innerJoin(stock).on(dividend1.stockId.eq(stock.id))
                .where(dividend1.exDividendDate.year().eq(year)
                        .and(dividend1.exDividendDate.month().eq(month))
                        .and(stock.ticker.eq(ticker)))
                .fetch();
    }

    @Override
    public List<Dividend> findAllByIdAndYearAndMonth(UUID id, Integer year, Integer month) {

        return queryFactory
                .selectFrom(dividend1)
                .innerJoin(stock).on(dividend1.stockId.eq(stock.id))
                .where(dividend1.exDividendDate.year().eq(year)
                        .and(dividend1.exDividendDate.month().eq(month))
                        .and(stock.id.eq(id)))
                .fetch();
    }

    @Override
    public List<Dividend> findAllByTickerAndYear(String ticker, Integer year) {

        return queryFactory
                .selectFrom(dividend1)
                .innerJoin(stock).on(dividend1.stockId.eq(stock.id))
                .where(dividend1.exDividendDate.year().eq(year)
                        .and(stock.ticker.eq(ticker)))
                .fetch();
    }

    @Override
    public List<Dividend> findAllByIdAndYear(UUID id, Integer year) {

        return queryFactory
                .selectFrom(dividend1)
                .innerJoin(stock).on(dividend1.stockId.eq(stock.id))
                .where(dividend1.exDividendDate.year().eq(year)
                        .and(stock.id.eq(id)))
                .fetch();
    }

    @Override
    public void deleteByYearAndCreatedAt(Integer year, Instant createdAt) {

        queryFactory
                .delete(dividend1)
                .where(dividend1.exDividendDate.year().eq(year)
                        .and(dividend1.createdAt.year().eq(InstantProvider.getYear(createdAt)))
                        .and(dividend1.createdAt.month().eq(InstantProvider.getMonth(createdAt)))
                        .and(dividend1.createdAt.dayOfMonth().eq(InstantProvider.getDayOfMonth(createdAt))))
                .execute();
    }


}
