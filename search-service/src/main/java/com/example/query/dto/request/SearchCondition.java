package com.example.query.dto.request;

import com.example.query.dto.Reliability;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

/***
 * public으로 선언된 생성자를 찾는다.
 * 없다면, public이 아닌 생성자 중에 매개변수 개수가 제일 적은 생성자를 선택한다. (보통 기본 생성자)
 * 찾은 생성자가 고유하다면, 해당 생성자를 선택한다.
 * 찾은 생성자가 여러개라면, 매개변수가 제일 적은 생성자를 선택한다.
 *
 */
@NoArgsConstructor
@Getter
@AllArgsConstructor
@ToString
public class SearchCondition {

    private String gu;
    private String dong;
    private String apartmentName;
    private Double areaForExclusiveUse;

    private LocalDate startDealDate;
    private LocalDate endDealDate;

    private Reliability reliability = Reliability.ALL;


    public boolean isGuEmpty() {
        return !StringUtils.hasText(gu);
    }

    public boolean isReliabilityEmpty() {
        return reliability == Reliability.ALL;
    }

    public BooleanExpression toReliabilityEq() {
        return reliability.getReliabilityExpression();
    }

    public boolean isNotValid() {
        return isDongNotValidate() || isApartmentNameNotValid() || isAreaNotValid();
    }

    private boolean isAreaNotValid() {
        return !StringUtils.hasText(apartmentName) && areaForExclusiveUse != null;
    }

    private boolean isApartmentNameNotValid() {
        return !StringUtils.hasText(dong) && StringUtils.hasText(apartmentName);
    }

    private boolean isDongNotValidate() {
        return isGuEmpty() && StringUtils.hasText(dong);
    }
}
