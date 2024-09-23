package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.entity.MarkerBoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardJPARepository extends JpaRepository<MarkerBoardEntity, Integer> {

    @Query("SELECT * FROM MarkerBoardEntity m " +
            "WHERE (m.shelter.shelterId = :placeId OR m.hospital.hospitalId = :placeId) " +
            "AND m.deleteYn = false")
    List<MarkerBoardEntity> findByPlaceIdAndDeleteYnFalse(@Param("placeId") String placeId);
}
