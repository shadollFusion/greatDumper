package com.experience.greatdumper.hprofdata.base;

import androidx.annotation.NonNull;


public class ObjectArray {

    private final ID objectId;
    private final int stackTraceSerial;
    private final ID elementClassId;
    private final int count;
    private final ID[] elements;

    public ObjectArray(ID objectId, int stackTraceSerial, ID elementClassId, int count, @NonNull ID[] elements) {
        this.objectId = objectId;
        this.stackTraceSerial = stackTraceSerial;
        this.elementClassId = elementClassId;
        this.count = count;
        this.elements = elements;
    }

    public ID getObjectId() {
        return objectId;
    }

    public int getStackTraceSerial() {
        return stackTraceSerial;
    }

    public ID getElementClassId() {
        return elementClassId;
    }

    public int getCount() {
        return count;
    }

    public ID[] getElements() {
        return elements;
    }
}
