package com.scit.proj.scitsainanguide.service;

import com.scit.proj.scitsainanguide.domain.dto.FriendDTO;
import com.scit.proj.scitsainanguide.repository.MyFriendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyFriendService {

    private final MyFriendRepository myFriendRepository;

    public Page<FriendDTO> selectMyFriendList(int page, int pageSize, String searchType, String searchWord, String memberId) {
        return myFriendRepository.selectMyFirendList(page, pageSize, searchType, searchWord, memberId);
    }

    public void updateFriend(String memberId, String friendId) {
        myFriendRepository.updateFriend(memberId, friendId);
    }

    public void deleteFriend(String memberId, String friendId) {
        // 양방향 관계를 모두 삭제해야하기 때문에 memberId, friendId 를 바꿔서 한 번더 삭제 로직을 실행한다.
        myFriendRepository.deleteFriend(memberId, friendId);
        myFriendRepository.deleteFriend(friendId, memberId);
    }
}
