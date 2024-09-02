package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.dto.FriendDTO;
import org.springframework.data.domain.Page;

public interface MyFriendRepository {

    Page<FriendDTO> selectMyFirendList(int page, int pageSize, String searchType, String searchWord, String memberId);

    Page<FriendDTO> selectMyFriendRequestList(int page, int pageSize, String memberId);

    void updateFriend(Integer relationId);

    void deleteFriend(String memberId, String friendId);

    void insertFriend(String memberId, String friendId, boolean friendYn);

    void acceptFriend(String memberId, Integer relationId);

    void rejectFriend(String memberId, Integer relationId);
}
