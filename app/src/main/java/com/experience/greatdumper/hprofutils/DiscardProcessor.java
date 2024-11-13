package com.experience.greatdumper.hprofutils;

import static com.experience.greatdumper.hprofdata.utils.StreamUtil.skip;

import androidx.annotation.NonNull;

import java.io.IOException;

public abstract class DiscardProcessor implements HprofProcessor {

    @Override
    public void onHeader(@NonNull String text, int idSize, int timeHigh, int timeLow) throws IOException {
    }

    @Override
    public void onRecord(int tag, int timestamp, int length, @NonNull HprofReader reader) throws IOException {
        skip(reader.getInputStream(), length);
    }
}