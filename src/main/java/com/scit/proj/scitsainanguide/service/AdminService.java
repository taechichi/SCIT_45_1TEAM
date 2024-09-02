package com.scit.proj.scitsainanguide.service;

import com.scit.proj.scitsainanguide.domain.dto.MemberDTO;
import com.scit.proj.scitsainanguide.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;

    public Page<MemberDTO> selectMemberList(int page, int pageSize
            , String filter, String filterWord, String searchTypeStr, String searchWord) {
        return memberRepository.selectMemberList(page, pageSize, filter, filterWord, searchTypeStr, searchWord);
    }

    public void updateMember(String memberId) {
        memberRepository.updateMember(memberId);
    }
}
