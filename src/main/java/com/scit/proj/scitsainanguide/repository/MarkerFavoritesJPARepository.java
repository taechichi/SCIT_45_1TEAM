package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.entity.MarkerFavoritesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarkerFavoritesJPARepository extends JpaRepository<MarkerFavoritesEntity, Integer> {
}
