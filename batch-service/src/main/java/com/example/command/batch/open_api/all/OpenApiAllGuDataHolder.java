package com.example.command.batch.open_api.all;

import com.example.command.batch.open_api.PostConstructInitiationBean;
import com.example.command.domain.dong.DongEntity;
import com.example.command.domain.dong.Gu;
import com.example.command.adapter.repository.dong.DongRepository;
import com.example.command.batch.open_api.DataHolder;
import com.example.command.batch.open_api.dto.GuDong;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OpenApiAllGuDataHolder implements DataHolder<GuDong>, PostConstructInitiationBean {

    private Map<Gu, Map<String, Integer>> guDongMap;
    private final DongRepository dongRepository;

    @PostConstruct
    @Override
    public void init() {
        guDongMap = dongRepository.findAll()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                DongEntity::getGu,
                                Collectors.toMap(
                                        DongEntity::getDongName,
                                        DongEntity::getId
                                )
                        )
                );
    }

    @Override
    public Integer getEntityId(GuDong guDong) {
        return guDongMap.get(guDong.gu()).get(guDong.dongName());
    }

    public Integer getEntityId(Gu gu, String dongName) {
        return guDongMap.get(gu).get(dongName);
    }
}
