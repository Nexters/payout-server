package nexters.payout.domain.stock.domain.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import nexters.payout.domain.stock.domain.QStock;
import nexters.payout.domain.stock.domain.Stock;

import java.util.List;

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

        long offset = (pageNumber - 1) * pageSize;

        return queryFactory.selectFrom(stock)
                .where(tickerStartsWith.or(nameContains))
                .orderBy(orderByPriority, orderByTicker, orderByName)
                .offset(offset)
                .limit(pageSize)
                .fetch();
    }
}
