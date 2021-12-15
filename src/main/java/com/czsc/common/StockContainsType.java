package com.czsc.common;

public enum StockContainsType {
    NOT_MERGED(1),
    MERGED(2);
    private int value;

    StockContainsType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
