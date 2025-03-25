package com.example.command.domain.apartment;
import com.example.command.domain.dong.DongEntity;
import com.example.command.domain.dong.Gu;
import com.example.command.domain.interest.Interest;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;

@Entity
@Table(name = "apartment_transaction")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ApartmentTransaction {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String apartmentName;
    private int buildYear;
    private int dealAmount;
    private int dealYear;
    private int dealMonth;
    private int dealDay;
    private double areaForExclusiveUse;
    private String jibun;
    private int floor;
    private LocalDate dealDate;

    @Enumerated(EnumType.STRING)
    private DealingGbn dealingGbn;

    @Column(columnDefinition = "GEOMETRY")
    private Point geography;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dong_entity_id")
    private DongEntity dongEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id")
    private Interest interest;

    public Gu getGu() {
        return dongEntity.getGu();
    }

    public String getDongName() {
        return dongEntity.getDongName();
    }
}