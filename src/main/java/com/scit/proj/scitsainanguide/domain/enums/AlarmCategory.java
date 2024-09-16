package com.scit.proj.scitsainanguide.domain.enums;

/**
 * 알림의 분류
 */
public enum AlarmCategory {

    MESSAGE_RECEIVE(1),
    FRIEND_REQUEST_RECEIVE(2),
    FRIEND_REQUEST_ACCEPT(3),
    EMERGENCY(4),
    DEFAULT(0);

    private final int value;

    AlarmCategory(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AlarmCategory fromValue(int value) {
        for (AlarmCategory type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return DEFAULT;
    }

    
}
