package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.entity.StatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusJpaRepository extends JpaRepository<StatusEntity, Integer> {
}
