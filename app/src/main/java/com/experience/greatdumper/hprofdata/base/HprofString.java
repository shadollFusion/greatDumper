package com.experience.greatdumper.hprofdata.base;

import androidx.annotation.NonNull;

public class HprofString extends Record {

    private ID id;
    private String value;

    public HprofString(ID id, @NonNull String value, int timestamp) {
        this.id = id;
        this.value = value;
        setTimestamp(timestamp);
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
    @NonNull
    public String getValue() {
        return value;
    }

    public void setValue(@NonNull String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "HPROF_STRING," + value + " (" + id + ")";
    }
}