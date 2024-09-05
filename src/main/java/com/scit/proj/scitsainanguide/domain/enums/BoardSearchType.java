package com.scit.proj.scitsainanguide.domain.enums;

/**
 *  관리자 > 삭제한 게시글 관리 페이지
 *  목록을 검색하는 데에 필요한 검색 조건
 */
public enum BoardSearchType {

    MEMBER_ID("memberId"),
    DELETE_REASON("deleteReason"),
    DEFAULT("");

    private final String value;

    BoardSearchType(String value) {
        this.value = value;
    }

    public static BoardSearchType fromValue(String value) {
        for (BoardSearchType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return DEFAULT;
    }

}
