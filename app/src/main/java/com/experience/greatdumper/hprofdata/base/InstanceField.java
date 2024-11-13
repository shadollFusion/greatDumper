package com.experience.greatdumper.hprofdata.base;

import androidx.annotation.NonNull;

public class InstanceField implements NamedField {

    private BasicType type;
    private ID nameId;

    public InstanceField(@NonNull BasicType type, ID nameId) {
        this.type = type;
        this.nameId = nameId;
    }

    @NonNull
    public BasicType getType() {
        return type;
    }

    public void setType(BasicType type) {
        this.type = type;
    }

    @Override
    public ID getFieldNameId() {
        return nameId;
    }

    @Override
    public void setFieldNameId(ID nameId) {
        this.nameId = nameId;
    }

    @Override
    public String toString() {
        return "InstanceField{" +
                "type=" + type +
                ", nameId=" + nameId +
                '}';
    }
}
