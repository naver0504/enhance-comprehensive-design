package com.example.command.domain;

import com.example.command.domain.dong.Gu;

import java.util.Optional;

public final class AddressUtils {
    private static final String SEOUL = "서울특별시";

    public static Optional<String> getJibunAddress(Gu gu, String dong, String jibun) {
        if(jibun == null) return Optional.empty();
        return Optional.of(concatJibunAddress(gu, dong, jibun));
    }

    private static String concatJibunAddress(Gu gu, String dong, String jibun) {
        return gu + " " + dong + " " + jibun;
    }

    @Deprecated
    public static String getRoadNameWithSeoulPrefixAndGu(Gu gu, String roadName, int roadNameBonbun, int roadNameBubun) {
        if(roadName == null) return null;
        return SEOUL + " " + getRoadNameWithGu(gu, roadName, roadNameBonbun, roadNameBubun);
    }

    @Deprecated
    public static String getRoadNameWithGu(Gu gu, String roadName, int roadNameBonbun, int roadNameBubun) {
        if(roadName == null) return null;

        return  gu + " " + concatRoadName(roadName, roadNameBonbun, roadNameBubun);
    }

    @Deprecated
    public static Optional<String> getRoadName(String roadName, int roadNameBonbun, int roadNameBubun) {
        if(roadName == null || roadNameBonbun == 0) return Optional.empty();
        return Optional.of(concatRoadName(roadName, roadNameBonbun, roadNameBubun));
    }

    @Deprecated
    private static String concatRoadName(String roadName, int roadNameBonbun, int roadNameBubun) {
        if(roadNameBonbun == 0) return roadName;
        return roadName + " " + roadNameBonbun  + getRoadNameBubun(roadNameBubun);
    }

    @Deprecated
    private static String getRoadNameBubun(int roadNameBubun) {
        return roadNameBubun == 0 ? "" : "-" + roadNameBubun;
    }
}
