package com.example.query.adapter.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class ApartmentTransactionRepositoryTest {

    @Autowired
    private ApartmentTransactionMongoRepository apartmentTransactionRepository;

    @Test
    void test() {

        boolean existsByTransactionId = apartmentTransactionRepository.isExistsByTransactionId(1);
        assertTrue(existsByTransactionId);
        existsByTransactionId = apartmentTransactionRepository.isExistsByTransactionId(2);
        assertFalse(existsByTransactionId);
    }


}