package com.example.query.adapter.document;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "apartment_transaction")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class ApartmentTransaction {

    @Id private String id;

    private long transactionId;
    private String apartmentName;
    private int buildYear;
    private int dealAmount;
    private double areaForExclusiveUse;
    private String jibun;
    private int floor;
    private LocalDate dealDate;
    private String dealingGbn;
    private Double latitude;
    private Double longitude;

    private String gu;
    private String dong;

    private long predictedCost;
    private boolean isReliable;
}
