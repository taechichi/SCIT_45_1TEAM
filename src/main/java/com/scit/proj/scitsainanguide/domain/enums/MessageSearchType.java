package com.scit.proj.scitsainanguide.domain.enums;

/**
 *  마이페이지 > 내 쪽지 목록 페이지
 *  목록을 검색하는 데에 필요한 검색 조건
 */
public enum MessageSearchType {

    SENDER_ID("senderId"),
    CONTENT("content"),
    DEFAULT("");

    private final String value;

    MessageSearchType(String value) {
        this.value = value;
    }

    public static MessageSearchType fromValue(String value) {
        for (MessageSearchType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return DEFAULT;
    }
}
