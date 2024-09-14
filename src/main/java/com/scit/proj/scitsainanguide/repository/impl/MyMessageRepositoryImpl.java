package com.scit.proj.scitsainanguide.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.scit.proj.scitsainanguide.domain.dto.MessageDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.domain.entity.MessageEntity;
import com.scit.proj.scitsainanguide.domain.entity.QMessageEntity;
import com.scit.proj.scitsainanguide.domain.enums.MessageSearchType;
import com.scit.proj.scitsainanguide.repository.MyMessageRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Transactional
public class MyMessageRepositoryImpl implements MyMessageRepository {

    @PersistenceContext
    private final EntityManager em; // JPA 영속성 관리!

    private final JPAQueryFactory queryFactory;

    private QMessageEntity message = QMessageEntity.messageEntity;

    @Autowired
    public MyMessageRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<MessageDTO> selectMyMessageList(SearchRequestDTO dto, String memberId) {
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getPageSize());
        MessageSearchType searchType = MessageSearchType.fromValue(dto.getSearchType());

        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(message.receiverId.eq(memberId)
                .and(message.deleteYn.eq(false)));

        // 동적 조건 추가
        // 1. 검색조건
        switch (searchType) {
            case SENDER_ID -> whereClause.and(message.senderId.contains(dto.getSearchWord()));
            case CONTENT -> whereClause.and(message.content.contains(dto.getSearchWord()));
        }

        // 쿼리 실행
        List<MessageDTO> messageDTOList = queryFactory.select(
                        Projections.constructor(MessageDTO.class,
                                message.messageId,
                                message.senderId,
                                message.content,
                                message.createDt,
                                message.sender.fileName
                        )
                )
                .from(message)
                .where(whereClause)
                .orderBy(message.createDt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 쿼리 (페이징을 위한 total count)
        long total = queryFactory.selectFrom(message)
                .where(whereClause)
                .fetchCount();

        return new PageImpl<>(messageDTOList, pageable, total);
    }

    /**
     * 내 쪽지 다중 삭제
     */
    @Override
    public void deleteMyMessage(String memberId, List<Integer> messageIdList) {
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(message.receiverId.eq(memberId))
                .and(message.messageId.in(messageIdList));

        executeDeleteMessageQuery(whereClause);
    }

    /**
     * 내 쪽지 단건 삭제
     */
    @Override
    public void deleteMyMessage(String memberId, Integer messageId) {
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(message.receiverId.eq(memberId))
                .and(message.messageId.eq(messageId));

        executeDeleteMessageQuery(whereClause);
    }

    @Override
    public void insertMyMessage(MessageDTO dto) {
        // 받는이가 여러 명일 수도 있는, 콤마로 구분된 receiverIds를 String list에 담기
        List<String> receiverIdList = Arrays.asList(dto.getReceiverId().split("\\s*,\\s*"));

        // message entity 를 생성
        for (String receiverId : receiverIdList) {
            MessageEntity messageEntity = MessageEntity.builder()
                    .senderId(dto.getSenderId())
                    .receiverId(receiverId)
                    .content(dto.getContent())
                    .deleteYn(false)
                    .build();
            // 엔티티 매니저를 통해 엔티티를 저장. - insert
            em.persist(messageEntity);
        }
    }

    @Override
    public Optional<MessageDTO> selectMyMessage(Integer messageId) {
        return Optional.ofNullable(
                queryFactory.select(
                        Projections.constructor(MessageDTO.class,
                                message.messageId,
                                message.senderId,
                                message.receiverId,
                                message.content,
                                message.createDt,
                                message.deleteYn,
                                message.sender.fileName
                        )
                )
                .from(message)
                .where(message.messageId.eq(messageId))
                .fetchOne()
        );
    }

    @Override
    public List<MessageDTO> selectMyUnreadMessageList(String memberId) {
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(message.receiverId.eq(memberId)
                .and(message.deleteYn.eq(false))
                .and(message.readDt.isNull()));

        return queryFactory.select(
                        Projections.constructor(MessageDTO.class,
                                message.messageId,
                                message.senderId,
                                message.sender.status.statusId,
                                message.content,
                                message.createDt,
                                message.readDt
                        )
                )
                .from(message)
                .where(whereClause)
                .orderBy(message.createDt.desc())
                .limit(5)   // 최대 5건의 쪽지까지만 확인 가능
                .fetch();
    }

    private void executeDeleteMessageQuery(BooleanBuilder whereClause) {
        // 삭제시 해당 메세지의 삭제여부 컬럼의 값만 true 로 변경
        queryFactory.update(message)
                .set(message.deleteYn, true)
                .where(whereClause)
                .execute();
    }

    private MessageDTO convertToMessageDTO(MessageEntity messageEntity) {
        return MessageDTO.builder()
                .messageId(messageEntity.getMessageId())
                .senderId(messageEntity.getSenderId())
                .receiverId(messageEntity.getReceiverId())
                .content(messageEntity.getContent())
                .createDt(messageEntity.getCreateDt())
                .deleteYn(messageEntity.getDeleteYn())
                .build();
    }
}


