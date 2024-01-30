package nexters.payout.domain.stock;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import nexters.payout.domain.QBaseEntity;


/**
 * QStock is a Querydsl query type for Stock
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStock extends EntityPathBase<Stock> {

    private static final long serialVersionUID = -1869313416L;

    public static final QStock stock = new QStock("stock");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    public final StringPath exchange = createString("exchange");

    //inherited
    public final ComparablePath<java.util.UUID> id = _super.id;

    public final StringPath industry = createString("industry");

    //inherited
    public final DateTimePath<java.time.Instant> lastModifiedAt = _super.lastModifiedAt;

    public final StringPath name = createString("name");

    public final NumberPath<Double> price = createNumber("price", Double.class);

    public final EnumPath<Sector> sector = createEnum("sector", Sector.class);

    public final StringPath ticker = createString("ticker");

    public final NumberPath<Integer> volume = createNumber("volume", Integer.class);

    public QStock(String variable) {
        super(Stock.class, forVariable(variable));
    }

    public QStock(Path<? extends Stock> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStock(PathMetadata metadata) {
        super(Stock.class, metadata);
    }

}

