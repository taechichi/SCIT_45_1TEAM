package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.entity.ShelterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelterRepository extends JpaRepository<ShelterEntity, Integer> {
    @Query(value = "SELECT shelter_name, latitude, longitude, " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * " +
            "cos(radians(longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(latitude)))) " +
            "AS distance " +
            "FROM shelter " +
            "HAVING distance < :radius " +
            "ORDER BY distance", nativeQuery = true)
    List<ShelterEntity> findNearbyShelters(@Param("latitude") double latitude,
                                           @Param("longitude") double longitude);
}
