package com.experience.greatdumper.hprofdata.base;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class StaticField implements NamedField {

    private BasicType type;

    private byte[] value;

    private ID fieldNameId;

    public StaticField(@NonNull BasicType type, @NonNull byte[] value, ID fieldNameId) {
        this.type = type;
        this.value = value;
        this.fieldNameId = fieldNameId;
    }

    @NonNull
    public BasicType getType() {
        return type;
    }

    public void setType(@NonNull BasicType type) {
        this.type = type;
    }

    @NonNull
    public byte[] getValue() {
        return value;
    }

    public void setValue(@NonNull byte[] value) {
        this.value = value;
    }

    @Override
    public ID getFieldNameId() {
        return fieldNameId;
    }

    @Override
    public void setFieldNameId(ID fieldNameId) {
        this.fieldNameId = fieldNameId;
    }

    @Override
    public String toString() {
        return "StaticField{" +
                "type=" + type +
                ", value=" + Arrays.toString(value) +
                ", fieldNameId=" + fieldNameId +
                '}';
    }
}

