package com.experience.greatdumper.hprofutils;

import androidx.annotation.NonNull;

import java.io.IOException;

public interface HeapDumpProcessor {

    /**
     * Callback method invoked when the HeapProfReader has read a new record.
     *
     * @param tag The tag identifying the record (see HeapTag)
     * @param reader A reference to the reader being used
     * @throws IOException
     */
    void onHeapRecord(int tag, @NonNull HeapDumpReader reader) throws IOException;

}