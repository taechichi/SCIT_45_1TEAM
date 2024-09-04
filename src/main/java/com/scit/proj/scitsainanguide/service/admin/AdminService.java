package com.scit.proj.scitsainanguide.service.admin;

import com.scit.proj.scitsainanguide.domain.dto.MemberDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
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

    public Page<MemberDTO> selectMemberList(SearchRequestDTO dto) {
        return memberRepository.selectMemberList(dto);
    }

    public void updateMember(String memberId) {
        memberRepository.updateMember(memberId);
    }

}
