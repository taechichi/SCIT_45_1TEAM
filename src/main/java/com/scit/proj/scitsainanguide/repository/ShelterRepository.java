package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.entity.ShelterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelterRepository extends JpaRepository<ShelterEntity, Integer> {
    @Query(value = "SELECT shelter_id, shelter_name, latitude, longitude, flood_yn, earthquake_yn, earth_yn, " +
            "hightide_yn, inland_flooding_yn, tsunami_yn, fire_yn, volcano_yn, lang_cd," +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * " +
            "cos(radians(longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(latitude)))) " +
            "AS distance " +
            "FROM shelter " +
            "HAVING distance < 0.5 " +
            "ORDER BY distance", nativeQuery = true)
    List<ShelterEntity> findNearbyShelters(@Param("latitude") double latitude,
                                           @Param("longitude") double longitude);

}
