package com.example.command.adapter.repository.dong;

import com.example.command.domain.dong.Gu;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static com.example.command.domain.dong.QDongEntity.dongEntity;


@Repository
@RequiredArgsConstructor
public class QuerydslDongRepository {

    private final JPAQueryFactory query;

    public Map<String, Integer> findByGuToMap(Gu gu){
        return query.selectFrom(dongEntity)
                .where(dongEntity.gu.eq(gu))
                .transform(GroupBy.groupBy(dongEntity.dongName).as(dongEntity.id));
    }

}
