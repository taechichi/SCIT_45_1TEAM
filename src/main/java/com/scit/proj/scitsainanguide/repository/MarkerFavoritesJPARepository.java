package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.entity.MarkerFavoritesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MarkerFavoritesJPARepository extends JpaRepository<MarkerFavoritesEntity, Integer> {

    // 첫 번째 쿼리: MemberEntity의 memberId와 HospitalEntity의 hospitalId를 이용한 검색
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END " +
            "FROM MarkerFavoritesEntity m " +
            "WHERE m.member.memberId = :memberId AND m.hospital.hospitalId = :hospitalId")
    boolean existsByMemberIdAndHospitalId(@Param("memberId") String memberId,
                                          @Param("hospitalId") String hospitalId);

    // 두 번째 쿼리: MemberEntity의 memberId와 ShelterEntity의 shelterId를 이용한 검색
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END " +
            "FROM MarkerFavoritesEntity m " +
            "WHERE m.member.memberId = :memberId AND m.shelter.shelterId = :shelterId")
    boolean existsByMemberIdAndShelterId(@Param("memberId") String memberId,
                                         @Param("shelterId") String shelterId);
}


