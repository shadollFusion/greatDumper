package com.experience.greatdumper.hprofutils;

import static com.experience.greatdumper.hprofdata.utils.StreamUtil.ID_SIZE;
import static com.experience.greatdumper.hprofdata.utils.StreamUtil.readByte;
import static com.experience.greatdumper.hprofdata.utils.StreamUtil.readID;
import static com.experience.greatdumper.hprofdata.utils.StreamUtil.readInt;
import static com.experience.greatdumper.hprofdata.utils.StreamUtil.readString;

import androidx.annotation.NonNull;

import com.experience.greatdumper.hprofdata.MemoryHprof;
import com.experience.greatdumper.hprofdata.base.BasicType;
import com.experience.greatdumper.hprofdata.base.ClassDefinition;
import com.experience.greatdumper.hprofdata.base.HprofString;
import com.experience.greatdumper.hprofdata.base.ID;
import com.experience.greatdumper.hprofdata.base.StackFrame;
import com.experience.greatdumper.hprofdata.utils.StreamUtil;
import com.google.firebase.crashlytics.buildtools.reloc.javax.annotation.Nonnull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class HprofReader {

    private final InputStream in;
    private final HprofProcessor processor;
    private int readCount;
    private int nextTag;

    public HprofReader(@Nonnull InputStream in, @Nonnull HprofProcessor processor) {
        this.in = in;
        this.processor = processor;
    }

    /**
     * Returns true if there is more data to be read (You can call next())
     *
     * @return True if there is more data to be read.
     * @throws IOException
     */
    public boolean hasNext() throws IOException {
        return nextTag != -1;
    }

    /**
     * Read the next record. This will trigger a callback to the processor.
     *
     * @throws IOException
     */
    public void next() throws IOException {
        if (readCount == 0) { // The header is always assumed to come first
            readHprofFileHeader();
        }
        else {
            readRecord();
        }
        readCount++;
        // Check if there are more records
        nextTag = readByte(in);
    }

    /**
     * Returns the InputStream that this HprofReader is reading its data from.
     *
     * @return The InputStream
     */
    @Nonnull
    public InputStream getInputStream() {
        return in;
    }

    /**
     * Read a LOAD_CLASS record and create a class definition for the loaded class.
     *
     * @return A ClassDefinition with some fields filled in (Serial number, class object id, stack trace serial & class name string id)
     * @throws IOException
     */
    @Nonnull
    public ClassDefinition readLoadClassRecord() throws IOException {
        int serialNumber = readInt(in);
        ID classObjectId = readID(in);
        int stackTraceSerial = readInt(in);
        ID classNameStringId = readID(in);
        ClassDefinition cls = new ClassDefinition();
        cls.setSerialNumber(serialNumber);
        cls.setObjectId(classObjectId);
        cls.setStackTraceSerial(stackTraceSerial);
        cls.setNameStringId(classNameStringId);
        return cls;
    }

    /**
     * Read a STRING record and create a HprofString based on its contents.
     *
     * @param recordLength Length of the record, part of the record header provided to HprofProcessor.onRecord()
     * @param timestamp    Timestamp of the record, part of the record header provided to HprofProcessor.onRecord()
     * @return A HprofString containing the string data
     * @throws IOException
     */
    @Nonnull
    public HprofString readStringRecord(int recordLength, int timestamp) throws IOException {
        ID id = readID(in);
        String string = readString(in, recordLength - ID_SIZE);
        return new HprofString(id, string, timestamp);
    }




    public StackFrame readStackFrame() throws IOException {

        StackFrame stackFrame = new StackFrame(readID(in),readID(in), readID(in),readID(in),readInt(in),readInt(in));

        return  stackFrame;

    }


    private void readRecord() throws IOException {
        int tagValue = nextTag;
        int time = readInt(in);
        int size = readInt(in);
        processor.onRecord(tagValue, time, size, this);
    }

    private void readHprofFileHeader() throws IOException {
        String text = StreamUtil.readNullTerminatedString(in);
        int idSize = readInt(in);
        // updating id size
        StreamUtil.ID_SIZE = idSize;
        BasicType.OBJECT.size=ID_SIZE;


//        if (idSize != 4) { // Currently only 4-byte ids are supported
//            throw new UnsupportedOperationException("Only hprof files with 4-byte ids can be read! This file has ids of " + idSize + " bytes");
//        }
        int timeHigh = readInt(in);
        int timeLow = readInt(in);
        processor.onHeader(text, idSize, timeHigh, timeLow);
    }

}