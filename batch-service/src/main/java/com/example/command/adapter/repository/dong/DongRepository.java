package com.example.command.adapter.repository.dong;

import com.example.command.domain.dong.DongEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DongRepository extends JpaRepository<DongEntity, Integer> {
}
