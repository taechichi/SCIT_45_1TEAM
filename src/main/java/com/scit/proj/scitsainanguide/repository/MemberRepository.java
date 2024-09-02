package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.dto.MemberDTO;
import org.springframework.data.domain.Page;

public interface MemberRepository {

    Page<MemberDTO> selectMemberList(int page, int pageSize, String filter, String filterWord, String searchTypeStr, String searchWord);

    void updateMember(String memberId);
}
