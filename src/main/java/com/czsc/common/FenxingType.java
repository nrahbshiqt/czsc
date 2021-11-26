package com.czsc.common;

public enum FenxingType {
    NONE_PART(0),
    TOP_PART(1),
    BOTTOM_PART(-1);
    private int value;

    FenxingType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
