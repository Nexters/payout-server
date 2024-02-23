package nexters.payout.domain.stock.infra;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import nexters.payout.domain.stock.domain.QStock;
import nexters.payout.domain.stock.domain.Stock;
import nexters.payout.domain.stock.infra.dto.StockDividendDto;
import nexters.payout.domain.stock.infra.dto.StockDividendYieldDto;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static nexters.payout.domain.dividend.domain.QDividend.dividend1;
import static nexters.payout.domain.stock.domain.QStock.stock;

@Repository
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Stock> findStocksByTickerOrNameWithPriority(String keyword, Integer pageNumber, Integer pageSize) {
        QStock stock = QStock.stock;

        // 검색 조건
        BooleanExpression tickerStartsWith = stock.ticker.startsWith(keyword);
        BooleanExpression nameContains = stock.name.contains(keyword);

        // 정렬 조건
        OrderSpecifier<Integer> orderByPriority = new CaseBuilder()
                .when(tickerStartsWith).then(1)
                .when(nameContains).then(2)
                .otherwise(3)
                .asc();
        OrderSpecifier<String> orderByTicker = stock.ticker.asc();
        OrderSpecifier<String> orderByName = stock.name.asc();

        long offset = (long) (pageNumber - 1) * pageSize;

        return queryFactory.selectFrom(stock)
                .where(tickerStartsWith.or(nameContains))
                .orderBy(orderByPriority, orderByTicker, orderByName)
                .offset(offset)
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<StockDividendDto> findUpcomingDividendStock(int pageNumber, int pageSize) {

        return queryFactory
                .select(Projections.constructor(StockDividendDto.class, stock, dividend1))
                .from(stock)
                .innerJoin(dividend1).on(stock.id.eq(dividend1.stockId))
                .where(dividend1.exDividendDate.after(LocalDateTime.now().toInstant(UTC)))
                .orderBy(dividend1.exDividendDate.asc())
                .offset((long) (pageNumber - 1) * pageSize)
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<StockDividendYieldDto> findBiggestDividendYieldStock(int lastYear, int pageNumber, int pageSize) {

        NumberExpression<Double> dividendYield = stock.price.divide(dividend1.dividend.sum().coalesce(0.0));

        return queryFactory
                .select(Projections.constructor(StockDividendYieldDto.class, stock, dividendYield))
                .from(stock)
                .leftJoin(dividend1)
                .on(stock.id.eq(dividend1.stockId))
                .where(dividend1.exDividendDate.year().eq(lastYear))
                .groupBy(stock.id, stock.price)
                .orderBy(dividendYield.desc())
                .offset((long) (pageNumber - 1) * pageSize)
                .limit(pageSize)
                .fetch();
    }
}
