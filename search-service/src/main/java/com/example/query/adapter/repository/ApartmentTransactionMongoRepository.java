package com.example.query.adapter.repository;

import com.example.query.adapter.document.ApartmentTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ApartmentTransactionMongoRepository extends MongoRepository<ApartmentTransaction, String> {
    Optional<ApartmentTransaction> findByTransactionId(Long transactionId);

    @Query(value = "{ 'gu' : ?0, 'dong' : ?1 }", fields = "{ 'apartmentName' : 1 }")
    List<String> findApartmentNames(String gu, String dongName);

    @Query(value = "{ 'gu' : ?0, 'dong' : ?1, 'apartmentName' : ?2 }", fields = "{ 'areaExclusive' : 1 }")
    List<Double> findAreaForExclusive(String gu, String dongName, String apartmentName);

    @Query(value = "{ 'gu' : ?0, 'dong' : ?1, 'apartmentName' : ?2, 'areaForExclusiveUse' : ?3, 'dealDate' : { $gte : ?4, $lte : ?5 } }")
    List<ApartmentTransaction> findApartmentTransactionsForGraph(String gu, String dongName, String apartmentName, double areaForExclusiveUse, LocalDate startDate, LocalDate endDate);
}
