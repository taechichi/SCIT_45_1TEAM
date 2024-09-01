package com.scit.proj.scitsainanguide.domain.enums;

/**
 *  마이페이지 > 친구 목록 페이지
 *  목록을 검색하는 데에 필요한 검색 조건
 */
public enum FriendSearchType {

    MEMBER_ID("memberId"),
    NICKNAME("nickname"),
    DEFAULT("");

    private final String value;

    FriendSearchType(String value) {
        this.value = value;
    }

    public static FriendSearchType fromValue(String value) {
        for (FriendSearchType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return DEFAULT;
    }

}
