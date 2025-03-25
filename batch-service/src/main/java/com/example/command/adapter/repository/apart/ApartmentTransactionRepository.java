package com.example.command.adapter.repository.apart;

import com.example.command.domain.apartment.ApartmentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApartmentTransactionRepository extends JpaRepository<ApartmentTransaction, Long> {
}