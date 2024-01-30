package nexters.dividend.domain.dividend;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDividend is a Querydsl query type for Dividend
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDividend extends EntityPathBase<Dividend> {

    private static final long serialVersionUID = -882282488L;

    public static final QDividend dividend1 = new QDividend("dividend1");

    public final nexters.dividend.domain.QBaseEntity _super = new nexters.dividend.domain.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    public final DateTimePath<java.time.Instant> declarationDate = createDateTime("declarationDate", java.time.Instant.class);

    public final NumberPath<Double> dividend = createNumber("dividend", Double.class);

    public final DateTimePath<java.time.Instant> exDividendDate = createDateTime("exDividendDate", java.time.Instant.class);

    //inherited
    public final ComparablePath<java.util.UUID> id = _super.id;

    //inherited
    public final DateTimePath<java.time.Instant> lastModifiedAt = _super.lastModifiedAt;

    public final DateTimePath<java.time.Instant> paymentDate = createDateTime("paymentDate", java.time.Instant.class);

    public final ComparablePath<java.util.UUID> stockId = createComparable("stockId", java.util.UUID.class);

    public QDividend(String variable) {
        super(Dividend.class, forVariable(variable));
    }

    public QDividend(Path<? extends Dividend> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDividend(PathMetadata metadata) {
        super(Dividend.class, metadata);
    }

}

