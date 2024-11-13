package com.experience.greatdumper.hprofutils;

import static com.experience.greatdumper.hprofdata.utils.StreamUtil.ID_SIZE;
import static com.experience.greatdumper.hprofdata.utils.StreamUtil.read;
import static com.experience.greatdumper.hprofdata.utils.StreamUtil.readByte;
import static com.experience.greatdumper.hprofdata.utils.StreamUtil.readID;
import static com.experience.greatdumper.hprofdata.utils.StreamUtil.readInt;
import static com.experience.greatdumper.hprofdata.utils.StreamUtil.readShort;
import static com.experience.greatdumper.hprofdata.utils.StreamUtil.skip;

import androidx.annotation.NonNull;

import com.experience.greatdumper.hprofdata.base.BasicType;
import com.experience.greatdumper.hprofdata.base.ClassDefinition;
import com.experience.greatdumper.hprofdata.base.ConstantField;
import com.experience.greatdumper.hprofdata.base.ID;
import com.experience.greatdumper.hprofdata.base.Instance;
import com.experience.greatdumper.hprofdata.base.InstanceField;
import com.experience.greatdumper.hprofdata.base.ObjectArray;
import com.experience.greatdumper.hprofdata.base.PrimitiveArray;
import com.experience.greatdumper.hprofdata.base.StaticField;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HeapDumpReader {

    private final CountingInputStream in;
    private final HeapDumpProcessor processor;
    private final int length;

    /**
     * Creates a reader to process a number of heap dump records (See HeapTag for types).
     *
     * @param in        InputStream to read the heap records from
     * @param length    Total length in bytes of the heap records to process
     * @param processor A callback interface that is invoked when a new record is encountered
     * @throws IOException
     */
    public HeapDumpReader(@NonNull InputStream in, int length, @NonNull HeapDumpProcessor processor) throws IOException {
        this.in = new CountingInputStream(in);
        this.processor = processor;
        this.length = length;
    }

    /**
     * Returns the InputStream used by this reader.
     *
     * @return The InputStream used by this reader
     */
    @NonNull
    public InputStream getInputStream() {
        return in;
    }

    /**
     * Returns true if there is more data to be read (You can call next())
     *
     * @return True if there is more data to be read.
     * @throws IOException
     */
    public boolean hasNext() throws IOException {
        return in.getCount() < length;
    }

    /**
     * Read the next record. This will trigger a callback to the processor.
     *
     * @throws IOException
     */
    public void next() throws IOException {
        int tag = in.read();
        processor.onHeapRecord(tag, this);
    }

    /**
     * Read a class dump record. The class definition should already have been created from a LOAD_CLASS record.
     *
     * @param loadedClasses Map of class ids and loaded classes. The class dump being read must be in this map
     */
    @NonNull
    public ClassDefinition readClassDumpRecord(Map<ID, ClassDefinition> loadedClasses) throws IOException {

//        System.out.println("readClassDumpRecord");

        ID objectId = readID(in);
        ClassDefinition cls = loadedClasses.get(objectId);
        if (cls == null) {
            throw new IllegalStateException("No class loaded for id " + objectId);
        }
        cls.setObjectId(objectId);
        cls.setStackTraceSerial(readInt(in));
        cls.setSuperClassObjectId(readID(in));
        cls.setClassLoaderObjectId(readID(in));
        cls.setSignersObjectId(readID(in));
        cls.setProtectionDomainObjectId(readID(in));

        skip(in, 2 * ID_SIZE); // Reserved data
//        skip(in, 8); // Reserved data
        cls.setInstanceSize(readInt(in));
        // Read constants fields
        short constantCount = readShort(in);
        if (constantCount > 0) {
            List<ConstantField> constantFields = new ArrayList<ConstantField>();
            cls.setConstantFields(constantFields);
            for (int i = 0; i < constantCount; i++) {
                short poolIndex = readShort(in);
                BasicType type = BasicType.fromType(readByte(in));
                byte[] value = read(in, type.size);
                constantFields.add(new ConstantField(poolIndex, type, value));

            }
        }
        // Read static fields
        short staticCount = readShort(in);
        if (staticCount > 0) {
            ArrayList<StaticField> staticFields = new ArrayList<StaticField>();
            cls.setStaticFields(staticFields);
            for (int i = 0; i < staticCount; i++) {
                ID nameId = readID(in);
                BasicType type = BasicType.fromType(readByte(in));
                byte[] value = read(in, type.size);
                staticFields.add(new StaticField(type, value, nameId));
            }
        }
        // Read instance fields
        short fieldCount = readShort(in);
        if (fieldCount > 0) {
            ArrayList<InstanceField> instanceFields = new ArrayList<InstanceField>();
            cls.setInstanceFields(instanceFields);
            for (int i = 0; i < fieldCount; i++) {
                ID nameId = readID(in);
                BasicType type = BasicType.fromType(readByte(in));
                instanceFields.add(new InstanceField(type, nameId));
            }
        }
//        System.out.println("cls=" + cls);
        return cls;
    }

    /**
     * Returns the current position of the underlying stream (in bytes from the start of the heap dump record).
     *
     * @return the current position
     */
    public long getCurrentPosition() {
        return in.getCount();
    }

    /**
     * Reads and returns an instance dump record.
     *
     * @return An Instance object containing all data from the record.
     */
    @NonNull
    public Instance readInstanceDump() throws IOException {
        ID objectId = readID(in);
        int stackTraceSerial = readInt(in);
        ID classId = readID(in);
        int length = readInt(in);
        byte[] data = read(in, length);
        return new Instance(objectId, stackTraceSerial, classId, data);
    }

    /**
     * Reads and returns a primitive array record;
     *
     * @return a primitive array record.
     */
    @NonNull
    public PrimitiveArray readPrimitiveArray() throws IOException {
        ID objectId = readID(in);
        int stackTraceSerial = readInt(in);
        int count = readInt(in);
        BasicType type = BasicType.fromType(in.read());
        byte[] arrayData = read(in, count * type.size);
        return new PrimitiveArray(objectId, stackTraceSerial, type, count, arrayData);
    }

    /**
     * Reads and returns an object array record;
     *
     * @return an object array record.
     */
    @NonNull
    public ObjectArray readObjectArray() throws IOException {
        ID objectId = readID(in);
        int stackTraceSerial = readInt(in);
        int count = readInt(in);
        ID elementClassId = readID(in);
        ID[] elements = new ID[count];
        for (int i = 0; i < count; i++) {
            elements[i] = readID(in);
        }
        return new ObjectArray(objectId, stackTraceSerial, elementClassId, count, elements);
    }

}
