package com.scit.proj.scitsainanguide.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.scit.proj.scitsainanguide.domain.dto.MessageDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.domain.entity.MessageEntity;
import com.scit.proj.scitsainanguide.domain.entity.QMessageEntity;
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

import java.util.List;

@Slf4j
@Repository
@Transactional
public class MyMessageRepositoryImpl implements MyMessageRepository {

    @PersistenceContext
    private final EntityManager em;

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

        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(message.receiver_id.eq(memberId)
                .and(message.deleteYn.eq(false)));

        List<MessageEntity> messageEntityList = queryFactory.selectFrom(message)
                .where(whereClause)
                .orderBy(message.createDt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 쿼리 (페이징을 위한 total count)
        long total = queryFactory.selectFrom(message)
                .where(whereClause)
                .fetchCount();

        // Entity -> DTO 변환 및 Page 반환
        List<MessageDTO> messageDTOList = messageEntityList.stream()
                .map(this::convertToMessageDTO)
                .toList();

        return new PageImpl<>(messageDTOList, pageable, total);
    }

    private MessageDTO convertToMessageDTO(MessageEntity messageEntity) {
        return MessageDTO.builder()
                .messageId(messageEntity.getMessageId())
                .senderId(messageEntity.getSender_id())
                .receiverId(messageEntity.getReceiver_id())
                .content(messageEntity.getContent())
                .createDt(messageEntity.getCreateDt())
                .deleteYn(messageEntity.getDeleteYn())
                .build();
    }
}
