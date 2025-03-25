package com.example.command.batch.kakao_map.dto;

import com.example.command.domain.AddressUtils;
import com.example.command.domain.dong.Gu;

import java.util.Optional;

public record TransactionWithGu(long id, Gu gu, String dong, String jibun) {

    public Optional<String> getJibunAddress() {
        return AddressUtils.getJibunAddress(gu, dong, jibun);
    }

    public String getResolvedJibunAddress() {
        return getJibunAddress().orElseThrow();
    }
}
