package com.example.command.adapter.repository.predict_cost;

import com.example.command.domain.predict_cost.PredictStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.command.domain.predict_cost.QPredictCost.predictCost;


@Repository
@RequiredArgsConstructor
public class QuerydslPredictCostRepository {

    private final JPAQueryFactory queryFactory;

    public void updateStatusToNotRecent(List<Long> ids) {
        queryFactory.update(predictCost)
                .set(predictCost.predictStatus, PredictStatus.NOT_RECENT)
                .where(predictCost.id.in(ids))
                .execute();
    }
}
