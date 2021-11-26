package com.czsc.common;

/**
 * 强度：1：最强，2：较强，3：一般，4：较弱
 */
public enum FenxingLevel {
    NONE(0),
    STRONGEST(1),
    STRONG(2),
    WEAKER(3),
    WEAKEST(4);

    private int value;

    FenxingLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
