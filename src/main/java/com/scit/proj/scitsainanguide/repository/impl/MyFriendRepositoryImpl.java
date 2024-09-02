package com.scit.proj.scitsainanguide.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.scit.proj.scitsainanguide.domain.dto.FriendDTO;
import com.scit.proj.scitsainanguide.domain.entity.FriendEntity;
import com.scit.proj.scitsainanguide.domain.entity.QFriendEntity;
import com.scit.proj.scitsainanguide.domain.entity.QMemberEntity;
import com.scit.proj.scitsainanguide.domain.enums.FriendSearchType;
import com.scit.proj.scitsainanguide.repository.MyFriendRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional
public class MyFriendRepositoryImpl implements MyFriendRepository {

    @PersistenceContext
    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    private QFriendEntity friend = QFriendEntity.friendEntity;

    @Autowired
    public MyFriendRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<FriendDTO> selectMyFirendList(int page, int pageSize
            , String searchTypeStr, String searchWord, String memberId) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        FriendSearchType searchType = FriendSearchType.fromValue(searchTypeStr);

        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(friend.memberId.eq(memberId))
                .and(friend.friendYn.eq(true))
                .and(friend.friend.withdraw.eq(false))
                .and(friend.friend.adminYn.eq(false));

        // 동적 조건 추가
        // 1. 검색조건
        switch (searchType) {
            case MEMBER_ID -> whereClause.and(friend.friendId.contains(searchWord));
            case NICKNAME -> whereClause.and(friend.member.nickname.contains(searchWord));
        }

        // 쿼리 실행
        List<FriendDTO> friendDTOList = queryFactory.select(
                            Projections.constructor(FriendDTO.class,
                            friend.friendId,
                            friend.friend.nickname,
                            friend.friend.nationality
                        )
                ).from(friend)
                //.join(member).on(friend.friendId.eq(member.memberId)) 조인 문 불필요
                .where(whereClause)
                .orderBy(
                    friend.favoriteYn.desc()  // 즐겨찾기한 친구 상위 노출
                    ,friend.friend.nickname.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 쿼리 (페이징을 위한 total count)
        long total = queryFactory.selectFrom(friend)
                .where(whereClause)
                .fetchCount();

        return new PageImpl<>(friendDTOList, pageable, total);
    }

    @Override
    public void updateFriend(Integer relationId) {
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(friend.relationId.eq(relationId));

        // FriendEntity 조회
        FriendEntity friendEntity = queryFactory.selectFrom(friend)
                .where(whereClause)
                .fetchOne();

        if (friendEntity == null) {
            throw new EntityNotFoundException("해당하는 친구 관계를 찾을 수 없습니다.");
        }

        // 즐겨찾기 업데이트
        queryFactory.update(friend)
                .set(friend.favoriteYn, true)
                .where(whereClause)
                .execute();
    }

    @Override
    public void deleteFriend(String memberId, String friendId) {
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(friend.friendId.eq(friendId))
                .and(friend.memberId.eq(memberId));

        // FriendEntity 조회
        FriendEntity friendEntity = queryFactory.selectFrom(friend)
                .where(whereClause)
                .fetchOne();

        if (friendEntity == null) {
            throw new EntityNotFoundException("해당하는 친구 관계를 찾을 수 없습니다.");
        }

        queryFactory.delete(friend)
                .where(whereClause)
                .execute();
    }

    @Override
    public void insertFriend(String memberId, String friendId, boolean friendYn) {
        // friend_member entity 를 생성
        FriendEntity friendEntity = new FriendEntity();
        friendEntity.setMemberId(memberId);
        friendEntity.setFriendId(friendId);
        friendEntity.setFavoriteYn(false);
        friendEntity.setFriendYn(friendYn);

        // 친구 신청을 수락하면서 친구 관계를 추가하는 경우 수락일시를 추가한다.
        if (friendYn) {
            friendEntity.setAcceptDt(LocalDateTime.now());
        }

        // 엔티티 매니저를 통해 엔티티를 저장.
        em.persist(friendEntity);
    }

    @Override
    public void acceptFriend(String memberId, Integer relationId) {
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(friend.relationId.eq(relationId));

        FriendEntity friendEntity = queryFactory.selectFrom(friend)
                .where(whereClause)
                .fetchOne();

        if (friendEntity == null) {
            throw new EntityNotFoundException("해당하는 친구 관계를 찾을 수 없습니다.");
        }

        // 즐겨찾기 업데이트
        queryFactory.update(friend)
                .set(friend.friendYn, true)
                .set(friend.acceptDt, LocalDateTime.now())
                .where(whereClause)
                .execute();

        // 양방향 관계를 맺기 위해 memberId, friendId 를 반대로한 새로운 관계를 생성
        insertFriend(memberId, friendEntity.getMemberId(), true);
    }

    @Override
    public void rejectFriend(String memberId, Integer relationId) {
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(friend.relationId.eq(relationId));

        FriendEntity friendEntity = queryFactory.selectFrom(friend)
                .where(whereClause)
                .fetchOne();

        if (friendEntity == null) {
            throw new EntityNotFoundException("해당하는 친구 관계를 찾을 수 없습니다.");
        }

        // 친구 관계를 삭제
        queryFactory.delete(friend)
                .where(whereClause)
                .execute();
    }
}
