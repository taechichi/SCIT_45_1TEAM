package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.dto.MemberDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import org.springframework.data.domain.Page;

public interface MemberRepository {

    Page<MemberDTO> selectMemberList(SearchRequestDTO dto);

    void updateMember(String memberId);
}
