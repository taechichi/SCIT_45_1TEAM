package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.entity.MarkerBoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardJPARepository extends JpaRepository<MarkerBoardEntity, Integer> {

    // 병원 ID로 검색하고 createDt 기준으로 최신 작성순 정렬
    @Query("SELECT b FROM MarkerBoardEntity b WHERE b.hospital.hospitalId = :hospitalId AND b.deleteYn = false ORDER BY b.createDt DESC")
    Page<MarkerBoardEntity> findByHospitalIdAndDeleteYnFalse(@Param("hospitalId") String hospitalId, Pageable pageable);

    // 대피소 ID로 검색하고 createDt 기준으로 최신 작성순 정렬
    @Query("SELECT b FROM MarkerBoardEntity b WHERE b.shelter.shelterId = :shelterId AND b.deleteYn = false ORDER BY b.createDt DESC")
    Page<MarkerBoardEntity> findByShelterIdAndDeleteYnFalse(@Param("shelterId") Integer shelterId, Pageable pageable);

}
