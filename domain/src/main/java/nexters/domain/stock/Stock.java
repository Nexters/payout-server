package nexters.domain.stock;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nexters.domain.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock extends BaseEntity {

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
}
