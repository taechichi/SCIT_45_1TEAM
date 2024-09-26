package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.dto.FriendDTO;
import com.scit.proj.scitsainanguide.domain.dto.MemberDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface MyFriendRepository {

    Page<FriendDTO> selectMyFriendList(SearchRequestDTO dto, String memberId);

    Page<FriendDTO> selectMyFriendRequestList(SearchRequestDTO dto, String memberId);

    void updateFriend(Integer relationId, String memberId);

    void deleteFriend(String memberId, String friendId);

    void insertFriend(String memberId, String friendId, boolean friendYn);

    String acceptFriend(String memberId, Integer relationId);

    void rejectFriend(String memberId, Integer relationId);

    Optional<MemberDTO> selectMyFriend(String memberId);

    List<MemberDTO> selectMyFavoriteList(String memberId);

    List<MemberDTO> selectMyFriendIdContainSearchWord(String memberId, String searchWord);
}
