package com.scit.proj.scitsainanguide.domain.enums;

import lombok.Getter;

/**
 *  관리자 > 회원 목록 페이지
 *  목록을 1차적으로 필터링 하려고하는 컬럼 기준
 */
@Getter
public enum MemberFilter {

    GENDER("gender"),
    NATIONALITY("nationality"),
    WITHDRAW("withdraw"),
    DEFAULT("");

    private final String value;

    MemberFilter(String value) {
        this.value = value;
    }

    public static MemberFilter fromValue(String value) {
        for (MemberFilter type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return DEFAULT;
    }
}
