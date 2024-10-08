package com.scit.proj.scitsainanguide.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.scit.proj.scitsainanguide.domain.dto.alarm.AlarmDTO;
import com.scit.proj.scitsainanguide.domain.dto.alarm.SelectAlarmResponseDTO;
import com.scit.proj.scitsainanguide.domain.entity.AlarmEntity;
import com.scit.proj.scitsainanguide.domain.entity.MemberEntity;
import com.scit.proj.scitsainanguide.domain.entity.QAlarmEntity;
import com.scit.proj.scitsainanguide.repository.AlarmRepository;
import com.scit.proj.scitsainanguide.repository.MemberJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class AlarmRepositoryImpl implements AlarmRepository {

    @PersistenceContext
    private final EntityManager em;

    private final JPAQueryFactory queryFactory;
    private final MemberJpaRepository memberRepository;

    private QAlarmEntity alarm = QAlarmEntity.alarmEntity;

    @Autowired
    public AlarmRepositoryImpl(EntityManager em, MemberJpaRepository memberRepository) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
        this.memberRepository = memberRepository;
    }

    @Override
    public void insertAlarm(String friendId, String message, Integer categoryId) {
        // alarm 을 받는 회원객체 조회
        MemberEntity memberEntity = memberRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 회원이 존재하지 않습니다."));

        AlarmEntity alarmEntity = AlarmEntity.builder()
                .categoryId(categoryId)
                .member(memberEntity)
                .contents(message)
                .readYn(false)
                .build();

        em.persist(alarmEntity);
    }

    @Override
    public SelectAlarmResponseDTO selectAlarmList(String memberId) {
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(alarm.readYn.eq(false)
                .and(alarm.member.memberId.eq(memberId)));

        // 알람 목록
        List<AlarmEntity> alarmEntityList = queryFactory.selectFrom(alarm)
                .where(whereClause)
                .orderBy(alarm.alarmDt.desc())
                .fetch();

        // 알람 목록 전체 카운트 쿼리
        long alarmCnt = queryFactory.selectFrom(alarm)
                .where(whereClause)
                .fetchCount();

        // Entity -> DTO 변환 및 Page 반환
        List<AlarmDTO> alarmDTOList = alarmEntityList.stream()
                .map(this::convertToAlarmDTO)
                .toList();

        return new SelectAlarmResponseDTO(alarmDTOList, alarmCnt);
    }

    @Override
    public void updateAlarmReadYn(String memberId, List<Integer> alarmIdList) {
        // 읽은 것으로 처리한다. (readYn = true 설정)
        queryFactory.update(alarm)
                .set(alarm.readYn, true)
                .where(alarm.readYn.eq(false)
                        .and(alarm.member.memberId.eq(memberId))
                )
                .execute();
    }

    private AlarmDTO convertToAlarmDTO(AlarmEntity alarmEntity) {
        return AlarmDTO.builder()
                .alarmId(alarmEntity.getAlarmId())
                .categoryId(alarmEntity.getCategoryId())
                .memberId(alarmEntity.getMember().getMemberId())
                .contents(alarmEntity.getContents())
                .alarmDt(alarmEntity.getAlarmDt())
                .readYn(alarmEntity.getReadYn())
                .build();
    }
}
