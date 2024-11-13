package com.experience.greatdumper.hprofdata.base;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class ConstantField {

    private final short poolIndex;
    private final BasicType type;
    private final byte[] value;

    public ConstantField(short poolIndex, @NonNull BasicType type, @NonNull byte[] value) {
        this.poolIndex = poolIndex;
        this.type = type;
        this.value = value;
    }

    public short getPoolIndex() {
        return poolIndex;
    }

    public BasicType getType() {
        return type;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ConstantField{" +
                "poolIndex=" + poolIndex +
                ", type=" + type +
                ", value=" + Arrays.toString(value) +
                '}';
    }
}