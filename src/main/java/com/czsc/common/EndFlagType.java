package com.czsc.common;

public enum EndFlagType {
    FINISHED(1),
    UNFINISHED(0);
    private int value;

    EndFlagType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
