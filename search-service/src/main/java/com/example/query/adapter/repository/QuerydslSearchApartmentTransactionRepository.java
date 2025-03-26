package com.example.query.adapter.repository;

import com.example.query.adapter.document.ApartmentTransaction;
import com.example.query.adapter.order.CustomPageable;
import com.example.query.dto.request.SearchCondition;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.query.adapter.document.QApartmentTransaction.apartmentTransaction;

@Repository
public class QuerydslSearchApartmentTransactionRepository extends QuerydslTransactionSupporter  {

    public QuerydslSearchApartmentTransactionRepository(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    public boolean isExistsByTransactionId(long transactionId) {


        return from(apartmentTransaction)
                .where(apartmentTransaction.transactionId.eq(transactionId))
                .fetchOne() != null;
    }

    @Cacheable(value = "apartmentTransaction", key = "'count' + ':' + #searchCondition.toString()")
    public Long getCount(SearchCondition searchCondition) {
        return from(apartmentTransaction)
                .where(
                        eqGu(searchCondition.getGu()),
                        eqDong(searchCondition.getDong()),
                        eqApartmentName(searchCondition.getApartmentName()),
                        eqAreaForExclusiveUse(searchCondition.getAreaForExclusiveUse()),
                        betweenDealDate(searchCondition.getStartDealDate(), searchCondition.getEndDealDate()),
                        searchCondition.toReliabilityEq()
                ).fetchCount();
    }


    public List<ApartmentTransaction> searchApartmentTransactions(SearchCondition searchCondition, CustomPageable customPageable) {
        return from(apartmentTransaction)
                .where(
                        eqGu(searchCondition.getGu()),
                        eqDong(searchCondition.getDong()),
                        eqApartmentName(searchCondition.getApartmentName()),
                        eqAreaForExclusiveUse(searchCondition.getAreaForExclusiveUse()),
                        betweenDealDate(searchCondition.getStartDealDate(), searchCondition.getEndDealDate()),
                        searchCondition.toReliabilityEq()
                )
                .orderBy(customPageable.orderBy())
                .limit(customPageable.getLimit())
                .offset(customPageable.getOffset())
                .fetch();
    }
}
