package nexters.dividend.domain.stock;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nexters.dividend.domain.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class Stock extends BaseEntity {

    @Column(unique = true, nullable = false, length = 10)
    private String ticker;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Sector sector;

    @ElementCollection
    private List<String> dividendCycle = new ArrayList<>();

    @Column(length = 10)
    private String exchange;

    private String industry;

    private Double price;

    private Integer volume;

    public Stock(String ticker, String name, Sector sector, List<String> dividendCycle, String exchange, String industry, Double price, Integer volume) {
        this.ticker = ticker;
        this.name = name;
        this.sector = sector;
        this.dividendCycle = dividendCycle;
        this.exchange = exchange;
        this.industry = industry;
        this.price = price;
        this.volume = volume;
    }
}
