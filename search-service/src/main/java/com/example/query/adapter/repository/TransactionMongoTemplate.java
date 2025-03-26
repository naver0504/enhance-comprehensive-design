package com.example.query.adapter.repository;

import com.example.query.adapter.document.ApartmentTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class TransactionMongoTemplate {

    private final MongoTemplate mongoTemplate;

    public List<String> findApartmentNames(String gu, String dongName) {
        Query query = new Query();
        query.addCriteria(eqGu(gu));
        query.addCriteria(eqDong(dongName));

        return mongoTemplate.getCollection("apartment_transaction")
                .distinct("apartmentName", query.getQueryObject(), String.class)
                .into(new java.util.ArrayList<>());
    }

    public List<Double> findAreaForExclusive(String gu, String dongName, String apartmentName) {
        Query query = new Query();
        query.addCriteria(eqGu(gu));
        query.addCriteria(eqDong(dongName));
        query.addCriteria(eqApartmentName(apartmentName));

        return mongoTemplate.getCollection("apartment_transaction")
                .distinct("areaForExclusiveUse", query.getQueryObject(), Double.class)
                .into(new java.util.ArrayList<>());
    }

    public void updatePredictCost(long transactionId, double predictCost, boolean isReliable) {
        Query query = new Query(eqTransactionId(transactionId));
        Update update = new Update().set("predictedCost", predictCost).set("isReliable", isReliable);
        mongoTemplate.updateFirst(query, update, ApartmentTransaction.class);
    }

    private Criteria eqTransactionId(long transactionId) {
        return Criteria.where("transactionId").is(transactionId);
    }

    private Criteria eqGu(String gu) {
        return Criteria.where("gu").is(gu);
    }

    private Criteria eqDong(String dong) {
        return Criteria.where("dong").is(dong);
    }

    private Criteria eqApartmentName(String apartmentName) {
        return Criteria.where("apartmentName").is(apartmentName);
    }
}
