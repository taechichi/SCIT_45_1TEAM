package com.scit.proj.scitsainanguide.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.scit.proj.scitsainanguide.domain.dto.FriendDTO;
import com.scit.proj.scitsainanguide.domain.dto.MemberDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.domain.entity.FriendEntity;
import com.scit.proj.scitsainanguide.domain.entity.QFriendEntity;
import com.scit.proj.scitsainanguide.domain.entity.QMemberEntity;
import com.scit.proj.scitsainanguide.domain.enums.FriendSearchType;
import com.scit.proj.scitsainanguide.repository.MyFriendRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Transactional
public class MyFriendRepositoryImpl implements MyFriendRepository {

    @PersistenceContext
    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    private QFriendEntity friend = QFriendEntity.friendEntity;
    private QMemberEntity member = QMemberEntity.memberEntity;

    @Autowired
    public MyFriendRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<FriendDTO> selectMyFriendList(SearchRequestDTO dto, String memberId) {
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getPageSize());
        FriendSearchType searchType = FriendSearchType.fromValue(dto.getSearchType());

        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(friend.memberId.eq(memberId))
                .and(friend.friendYn.eq(true))
                .and(friend.friend.withdraw.eq(false))
                .and(friend.friend.adminYn.eq(false));

        // 동적 조건 추가
        // 1. 검색조건
        switch (searchType) {
            case FRIEND_ID -> whereClause.and(friend.friendId.contains(dto.getSearchWord()));
            case NICKNAME -> whereClause.and(friend.friend.nickname.contains(dto.getSearchWord()));
        }

        // 쿼리 실행
        List<FriendDTO> friendDTOList = queryFactory.select(
                            Projections.constructor(FriendDTO.class,
                            friend.relationId,
                            friend.friendId,
                            friend.friend.nickname,
                            friend.favoriteYn,
                            friend.friend.nationality,
                            friend.friend.status.statusName,
                            friend.friend.status.statusNameJa
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
        return new PageImpl<>(friendDTOList, pageable, getTotalCount(whereClause));
    }

    @Override
    public Page<FriendDTO> selectMyFriendRequestList(SearchRequestDTO dto, String memberId) {
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getPageSize());

        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(friend.memberId.eq(memberId))
                .and(friend.friendYn.eq(false))
                .and(friend.friend.withdraw.eq(false))
                .and(friend.friend.adminYn.eq(false));

        // 쿼리 실행
        List<FriendDTO> friendRequestDTOList = queryFactory.select(
                        Projections.constructor(FriendDTO.class,
                                friend.relationId,
                                friend.friendId,
                                friend.friend.nickname,
                                friend.friend.nationality,
                                friend.requestDt
                        )
                ).from(friend)
                .where(whereClause)
                .orderBy(
                        friend.requestDt.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 쿼리 (페이징을 위한 total count)
        return new PageImpl<>(friendRequestDTOList, pageable, getTotalCount(whereClause));
    }

    @Override
    public void updateFriend(Integer relationId, String memberId) {
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(friend.relationId.eq(relationId));

        // FriendEntity 조회
        FriendEntity friendEntity = queryFactory.selectFrom(friend)
                .where(whereClause)
                .fetchOne();

        if (friendEntity == null) {
            throw new EntityNotFoundException("해당하는 친구 관계를 찾을 수 없습니다.");
        }
        boolean favoriteYn = friendEntity.getFavoriteYn();

        // 친구 즐겨찾기 추가 시에는 최대 5명까지 가능하기 때문에 유효성검사를 먼저 한다.
        if(!favoriteYn && getTotalCount(whereClause) == 5L) {
            throw new IllegalStateException("친구 즐겨찾기는 최대 5명까지만 가능합니다.");
        }

        // 즐겨찾기 업데이트 (즐겨찾기 추가 / 취소)
        queryFactory.update(friend)
                .set(friend.favoriteYn, !favoriteYn)
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

        executeDeleteFriendQuery(whereClause);
    }

    @Override
    public void insertFriend(String memberId, String friendId, boolean friendYn) {
        // 친구 요청 거는 대상이 여러명일 수도 있음. 콤마로 아이디가 구분된 friendId를 list에 담음
        List<String> friendIdList = Arrays.asList(friendId.split("\\s*,\\s*"));

        // friend_member entity 를 생성
        for (String fId : friendIdList) {
            boolean exists = queryFactory.selectFrom(friend)
                    .where(friend.memberId.eq(memberId)
                            .and(friend.friendId.eq(fId)))
                    .fetchOne() != null;  // fetchOne()로 존재 여부 확인

            // 친구 관계가 이미 존재하면 에러 발생
            if (exists) {
                throw new IllegalArgumentException(fId + "님과 이미 친구사이이거나, 이미 친구 요청을 보낸 상태입니다.");
            }

            FriendEntity friendEntity = FriendEntity.builder()
                    .memberId(memberId)
                    .friendId(fId)
                    .favoriteYn(false)
                    .friendYn(friendYn)
                    .build();

            // 친구 신청을 수락하면서 친구 관계를 추가하는 경우 수락일시를 추가한다.
            if (friendYn) {
                friendEntity.setAcceptDt(LocalDateTime.now());
            }

            // 엔티티 매니저를 통해 엔티티를 저장.
            em.persist(friendEntity);
        }
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
        executeDeleteFriendQuery(whereClause);
    }

    @Override
    public Optional<MemberDTO> selectMyFriend(String memberId) {
        return Optional.ofNullable(
                queryFactory.select(
                        Projections.constructor(MemberDTO.class,
                                member.memberId,
                                member.nickname,
                                member.lastStUpdateDt,
                                member.status.statusName,
                                member.status.statusNameJa
                        )
                )
                .from(member)
                .where(member.memberId.eq(memberId))
                .fetchOne()
        );
    }

    @Override
    public List<MemberDTO> selectMyFavoriteList(String memberId) {
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(friend.memberId.eq(memberId))
                .and(friend.friendYn.eq(true))
                .and(friend.favoriteYn.eq(true))
                .and(friend.friend.withdraw.eq(false))
                .and(friend.friend.adminYn.eq(false));

        // 로그인한 회원의 즐겨찾기한 친구 목록 조회
        List<FriendEntity> friendEntityList = queryFactory.selectFrom(friend)
                .where(whereClause)
                .fetch();

        // 친구 Entity 에서 id 를 추출해서 List 로 만든다.
        List<String> friendIdList = friendEntityList.stream()
                .map(FriendEntity::getFriendId)
                .toList();

        // 해당 친구들의 정보 조회
        return queryFactory.select(
                Projections.constructor(MemberDTO.class,
                        member.memberId,
                        member.nickname,
                        member.fileName,
                        member.status.statusId,
                        member.status.statusName,
                        member.status.statusNameJa,
                        member.lastStUpdateDt,
                        member.stMessage
                )
        )
        .from(member)
        .where(member.memberId.in(friendIdList))
        .fetch();
    }

    /**
     * 여러 명의 사용자를 대상으로 한 번에 쪽지 보내기
     * @param memberId
     * @param searchWord
     * @return
     */
    @Override
    public List<MemberDTO> selectMyFriendIdContainSearchWord(String memberId, String searchWord){

        // 현재 로그인 중인 사용자의 친구들 중, 사용자가 검색한 단어가 포함된 id 리스트를 불러옴
        List<String> myFriendList =  queryFactory.select(friend.friendId)
                .from(friend)
                .where(friend.memberId.eq(memberId).and(friend.friendId.contains(searchWord)))
                .orderBy(friend.friendId.asc())
                .fetch();

        // 위에서 불러온 리스트 내 friendId로 member table 내 정보 가져옴
        return queryFactory.select(
                        Projections.constructor(MemberDTO.class,
                                member.memberId,
                                member.nickname,
                                member.fileName,
                                member.gender,
                                member.nationality,
                                friend.favoriteYn
                        )
                )
                .from(member)
                .join(friend).on(friend.friendId.eq(member.memberId))
                .where(friend.memberId.eq(memberId).and(member.memberId.in(myFriendList)))
                .orderBy(friend.favoriteYn.desc(),(member.memberId.asc()))
                .fetch();
    }


    private long getTotalCount(BooleanBuilder whereClause) {
        return queryFactory.selectFrom(friend)
                .where(whereClause)
                .fetchCount();
    }

    private void executeDeleteFriendQuery(BooleanBuilder whereClause) {
        queryFactory.delete(friend)
                .where(whereClause)
                .execute();
    }
}
