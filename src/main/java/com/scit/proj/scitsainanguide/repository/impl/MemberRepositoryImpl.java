package com.scit.proj.scitsainanguide.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.domain.enums.MemberFilter;
import com.scit.proj.scitsainanguide.domain.enums.MemberSearchType;
import com.scit.proj.scitsainanguide.domain.dto.MemberDTO;
import com.scit.proj.scitsainanguide.domain.entity.MemberEntity;
import com.scit.proj.scitsainanguide.domain.entity.QMemberEntity;
import com.scit.proj.scitsainanguide.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class MemberRepositoryImpl implements MemberRepository {

    @PersistenceContext
    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    private QMemberEntity member = QMemberEntity.memberEntity;

    @Autowired
    public MemberRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<MemberDTO> selectMemberList(SearchRequestDTO dto) {
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getPageSize());
        MemberSearchType searchType = MemberSearchType.fromValue(dto.getSearchType());
        MemberFilter filter = MemberFilter.fromValue(dto.getFilter());

        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(member.withdraw.eq("true".equals(dto.getFilterWord())))
                .and(member.adminYn.eq(false));

        // 동적 조건 추가
        // 1. 검색조건
        switch (searchType) {
            case MEMBER_ID -> whereClause.and(member.memberId.contains(dto.getSearchWord()));
            case NICKNAME -> whereClause.and(member.nickname.contains(dto.getSearchWord()));
            case EMAIL -> whereClause.and(member.email.contains(dto.getSearchWord()));
        }
        // 2. 필터 (회원 탈퇴여부는 초기 whereClause 설정시 구현)
        switch (filter) {
            case GENDER -> whereClause.and(member.gender.eq(dto.getFilterWord()));
            case NATIONALITY -> whereClause.and(member.nationality.eq(dto.getFilterWord()));
        }

        // 쿼리 실행
        List<MemberEntity> memberEntityList = queryFactory.selectFrom(member)
                .where(whereClause)
                .orderBy(member.memberId.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 쿼리 (페이징을 위한 total count)
        long total = queryFactory.selectFrom(member)
                .where(whereClause)
                .fetchCount();

        // Entity -> DTO 변환 및 Page 반환
        List<MemberDTO> memberDTOList = memberEntityList.stream()
                .map(this::convertToMemberDTO)
                .toList();

        return new PageImpl<>(memberDTOList, pageable, total);
    }

    @Override
    public void updateMember(String memberId) {
        // MemberEntity 조회
        MemberEntity memberEntity = queryFactory.selectFrom(member)
                .where(member.memberId.eq(memberId))
                .fetchOne();

        if (memberEntity == null) {
            throw new EntityNotFoundException("해당하는 회원을 찾을 수 없습니다.");
        }

        // 관리자 여부 업데이트
        queryFactory.update(member)
                .set(member.adminYn, true)
                .where(member.memberId.eq(memberId))
                .execute();
    }

    private MemberDTO convertToMemberDTO(MemberEntity memberEntity) {
        return MemberDTO.builder()
                .memberId(memberEntity.getMemberId())
                .password(memberEntity.getPassword())
                .nickname(memberEntity.getNickname())
                .gender(memberEntity.getGender())
                .nationality(memberEntity.getNationality())
                .email(memberEntity.getEmail())
                .phone(memberEntity.getPhone())
                .statusName(memberEntity.getStatus().getStatusName())
                .withdraw(memberEntity.getWithdraw())
                .build();
    }
}
