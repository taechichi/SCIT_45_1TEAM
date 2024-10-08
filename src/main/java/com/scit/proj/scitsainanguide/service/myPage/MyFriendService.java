package com.scit.proj.scitsainanguide.service.myPage;

import com.scit.proj.scitsainanguide.domain.dto.FriendDTO;
import com.scit.proj.scitsainanguide.domain.dto.MemberDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.repository.MyFriendRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyFriendService {

    private final MyFriendRepository myFriendRepository;

    public Page<FriendDTO> selectMyFriendList(SearchRequestDTO dto, String memberId) {
        return myFriendRepository.selectMyFriendList(dto, memberId);
    }

    public Page<FriendDTO> selectMyFriendRequestList(SearchRequestDTO dto, String memberId) {
        return myFriendRepository.selectMyFriendRequestList(dto, memberId);
    }

    public void updateFriend(Integer relationId, String memberId) {
        myFriendRepository.updateFriend(relationId, memberId);
    }

    public void deleteFriend(String memberId, String friendId) {
        // 양방향 관계를 모두 삭제해야하기 때문에 memberId, friendId 를 바꿔서 한 번더 삭제 로직을 실행한다.
        myFriendRepository.deleteFriend(memberId, friendId);
        myFriendRepository.deleteFriend(friendId, memberId);
    }

    // 친구 추가 메서드
    public void insertFriend(String memberId, String friendId) {
        myFriendRepository.insertFriend(memberId, friendId, false);
    }

    public String acceptFriend(String memberId, Integer relationId) {
        return myFriendRepository.acceptFriend(memberId, relationId);
    }

    public void rejectFriend(String memberId, Integer relationId) {
        myFriendRepository.rejectFriend(memberId, relationId);
    }

    public MemberDTO selectMyFriend(String memberId) {
        return myFriendRepository.selectMyFriend(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 회원을 찾을 수 없습니다."));
    }

    public List<MemberDTO> selectMyFavoriteList(String memberId) {
        return myFriendRepository.selectMyFavoriteList(memberId);
    }

    public List<MemberDTO> selectMyFriendList(String memberId) {
        return myFriendRepository.selectMyFriendList(memberId);
    }

    // 사용자가 친구 검색에서 입력한 아이디가 포함된 아이디를 가진 친구 리스트를 반환한다.
    public List<MemberDTO> selectMyFriendIdContainSearchWord(String memberId, String searchWord){
        return myFriendRepository.selectMyFriendIdContainSearchWord(memberId, searchWord);
    }

}
