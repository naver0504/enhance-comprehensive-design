package com.example.command.domain.dong;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dong")
@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class DongEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String dongCode;

    private String dongName;

    @Enumerated(EnumType.STRING)
    private Gu gu;

    private String guCode;

    @Builder
    public DongEntity(String dongCode, String dongName, Gu gu) {
        this.dongCode = dongCode;
        this.dongName = dongName;
        this.gu = gu;
        this.guCode = gu.getRegionalCode();
    }
}
