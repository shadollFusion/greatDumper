package com.experience.greatdumper.hprofdata.base;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class PrimitiveArray {

    private final ID objectId;
    private final int stackTraceSerial;
    private final BasicType type;
    private final int count;
    private final byte[] arrayData;

    public PrimitiveArray(ID objectId, int stackTraceSerial, @NonNull BasicType type, int count, @NonNull byte[] arrayData) {
        this.objectId = objectId;
        this.stackTraceSerial = stackTraceSerial;
        this.type = type;
        this.count = count;
        this.arrayData = arrayData;
    }

    public ID getObjectId() {
        return objectId;
    }

    public int getStackTraceSerial() {
        return stackTraceSerial;
    }

    public BasicType getType() {
        return type;
    }

    public int getCount() {
        return count;
    }

    public byte[] getArrayData() {
        return arrayData;
    }


    @Override
    public String toString() {
        return "PrimitiveArray{" +
                "objectId=" + objectId +
                ", stackTraceSerial=" + stackTraceSerial +
                ", type=" + type +
                ", count=" + count +
                ", arrayData=" + Arrays.toString(arrayData) +
                '}';
    }
}