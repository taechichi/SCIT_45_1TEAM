package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.entity.MarkerFavoritesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MarkerFavoritesJPARepository extends JpaRepository<MarkerFavoritesEntity, Integer> {

    //병원 즐겨찾기 검색
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END " +
            "FROM MarkerFavoritesEntity m " +
            "WHERE m.member.memberId = :memberId AND m.hospital.hospitalId = :hospitalId")
    boolean existsByMemberIdAndHospitalId(@Param("memberId") String memberId,
                                          @Param("hospitalId") String hospitalId);

    //쉘터 즐겨찾기 검색
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END " +
            "FROM MarkerFavoritesEntity m " +
            "WHERE m.member.memberId = :memberId AND m.shelter.shelterId = :shelterId")
    boolean existsByMemberIdAndShelterId(@Param("memberId") String memberId,
                                         @Param("shelterId") String shelterId);

    // 병원 즐겨찾기 삭제
    @Modifying
    @Query("DELETE FROM MarkerFavoritesEntity m WHERE m.member.memberId = :memberId AND m.hospital.hospitalId = :placeId")
    void deleteByMemberIdAndHospitalId(@Param("memberId") String memberId,
                                       @Param("placeId") String placeId);

    // 쉘터 즐겨찾기 삭제
    @Modifying
    @Query("DELETE FROM MarkerFavoritesEntity m WHERE m.member.memberId = :memberId AND m.shelter.shelterId = :placeId")
    void deleteByMemberIdAndShelterId(@Param("memberId") String memberId,
                                      @Param("placeId") String placeId);
}


