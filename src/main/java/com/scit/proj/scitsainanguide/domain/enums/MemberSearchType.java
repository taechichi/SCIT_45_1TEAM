package com.scit.proj.scitsainanguide.domain.enums;

import lombok.Getter;

/**
 *  관리자 > 회원 목록 페이지
 *  목록을 검색하는 데에 필요한 검색 조건
 */
@Getter
public enum MemberSearchType {

    MEMBER_ID("memberId"),
    NICKNAME("nickname"),
    EMAIL("email"),
    DEFAULT("");

    private final String value;

    MemberSearchType(String value) {
        this.value = value;
    }

    public static MemberSearchType fromValue(String value) {
        for (MemberSearchType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return DEFAULT;
    }
}
