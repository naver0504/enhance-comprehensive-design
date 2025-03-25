package com.example.command.domain.dong;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public enum Gu {


    강남구("11680"), 강동구("11740"), 강북구("11305"), 강서구("11500"),
    관악구("11620"), 광진구("11215"), 구로구("11530"), 금천구("11545"),
    노원구("11350"), 도봉구("11320"), 동대문구("11230"), 동작구("11590"),
    마포구("11440"), 서대문구("11410"), 서초구("11650"), 성동구("11200"),
    성북구("11290"), 송파구("11710"), 양천구("11470"), 영등포구("11560"),
    용산구("11170"), 은평구("11380"), 종로구("11110"), 중구("11140"),
    중랑구("11260"), NONE("none");

    private final String regionalCode;
    public static final List<Gu> guList = List.of(Gu.values()).subList(0, Gu.values().length - 1);

    public static Gu getGuFromRegionalCode(String regionalCode) {
        return guList.stream()
                .filter(gu -> Objects.equals(gu.getRegionalCode(), regionalCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 구가 없습니다. " + regionalCode));
    }

    public static Gu fromOrdinal(int ordinal) {
        return guList.get(ordinal);
    }
}
