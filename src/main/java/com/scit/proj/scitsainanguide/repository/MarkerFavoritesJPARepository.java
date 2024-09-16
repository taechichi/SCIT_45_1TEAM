package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.entity.MarkerFavoritesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarkerFavoritesJPARepository extends JpaRepository<MarkerFavoritesEntity, Integer> {
}
