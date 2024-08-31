package com.scit.proj.scitsainanguide.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.scit.proj.scitsainanguide.domain.MemberFilter;
import com.scit.proj.scitsainanguide.domain.MemberSearchType;
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

    @Autowired
    public MemberRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<MemberDTO> selectMemberList(int page, int pageSize, String filterStr, String filterWord, String searchTypeStr, String searchWord) {

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.Direction.ASC, "memberId");
        MemberSearchType searchType = MemberSearchType.fromValue(searchTypeStr);
        MemberFilter filter = MemberFilter.fromValue(filterStr);

        QMemberEntity member = QMemberEntity.memberEntity;

        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(member.withdraw.eq("true".equals(filterWord)))
                .and(member.adminYn.eq(false));

        // 동적 조건 추가
        // 1. 검색조건
        switch (searchType) {
            case MEMBER_ID -> whereClause.and(member.memberId.contains(searchWord));
            case NICKNAME -> whereClause.and(member.nickname.contains(searchWord));
            case EMAIL -> whereClause.and(member.email.contains(searchWord));
        }
        // 2. 필터
        switch (filter) {
            case GENDER -> whereClause.and(member.gender.eq(filterWord));
            case NATIONALITY -> whereClause.and(member.nationality.eq(filterWord));
        }

        // 쿼리 실행
        List<MemberEntity> memberEntities = queryFactory.selectFrom(member)
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
        List<MemberDTO> memberDTOList = memberEntities.stream()
                .map(this::convertToMemberDTO)
                .toList();

        return new PageImpl<>(memberDTOList, pageable, total);
    }

    @Transactional
    public void updateMember(String memberId) {
        QMemberEntity qMember = QMemberEntity.memberEntity;

        // MemberEntity 조회
        MemberEntity memberEntity = queryFactory.selectFrom(qMember)
                .where(qMember.memberId.eq(memberId))
                .fetchOne();

        if (memberEntity == null) {
            throw new EntityNotFoundException("해당하는 회원을 찾을 수 없습니다.");
        }

        // 관리자 여부 업데이트
        queryFactory.update(qMember)
                .set(qMember.adminYn, true)
                .where(qMember.memberId.eq(memberId))
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
