package nexters.domain.stock;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(unique = true)
    private String ticker;

    private String name;

    @Enumerated(EnumType.STRING)
    private Sector sector;

    private String exchange;

    private String industry;

    private double price;

    private Double volume;
}
