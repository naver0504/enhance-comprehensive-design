package com.example.command.adapter.repository.apart;

import com.example.command.domain.apartment.ApartmentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ApartmentTransactionRepository extends JpaRepository<ApartmentTransaction, Long> {

    @Query("SELECT ID FROM ApartmentTransaction ORDER BY ID DESC LIMIT 1")
    Long findLastId();
}