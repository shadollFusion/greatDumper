package com.experience.greatdumper.hprofutils;

import com.google.firebase.crashlytics.buildtools.reloc.javax.annotation.Nonnull;

import java.io.IOException;

public class HeapDumpDiscardProcessor extends HeapDumpBaseProcessor {

    @Override
    public void onHeapRecord(int tag, @Nonnull HeapDumpReader reader) throws IOException {
        skipHeapRecord(tag, reader.getInputStream());
    }
}