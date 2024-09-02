package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.dto.FriendDTO;
import org.springframework.data.domain.Page;

public interface MyFriendRepository {

    Page<FriendDTO> selectMyFirendList(int page, int pageSize, String searchType, String searchWord, String memberId);

    void updateFriend(String memberId, String friendId);

    void deleteFriend(String memberId, String friendId);
}
