package com.experience.greatdumper.hprofutils;

import androidx.annotation.NonNull;

import com.experience.greatdumper.hprofdata.base.ClassDefinition;
import com.experience.greatdumper.hprofdata.base.HprofString;
import com.experience.greatdumper.hprofdata.base.ID;
import com.experience.greatdumper.hprofdata.base.Instance;
import com.experience.greatdumper.hprofdata.base.ObjectArray;
import com.experience.greatdumper.hprofdata.base.PrimitiveArray;
import com.google.firebase.crashlytics.buildtools.reloc.javax.annotation.Nonnull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * HPROF processor for finding data (classes definitions and classinfo) of Views and ViewGroups
 */
public class ViewDataProcessor extends DiscardProcessor {

    private Map<ID, HprofString> strings = new HashMap<ID, HprofString>();
    private Map<ID, ClassDefinition> classes = new HashMap<ID, ClassDefinition>();
    private Map<ID, ObjectArray> objArrays = new HashMap<ID, ObjectArray>();
    private Map<ID, PrimitiveArray> primitiveArrays = new HashMap<ID, PrimitiveArray>();
    private Map<ID, Instance> instances = new HashMap<ID, Instance>();
    private HeapDumpDiscardProcessor heapDumpProcessor = new HeapDumpDiscardProcessor() {

        @Override
        public void onHeapRecord(int tag, @Nonnull HeapDumpReader reader) throws IOException {
            switch (tag) {
                case HeapTag.CLASS_DUMP: {
                    reader.readClassDumpRecord(classes);
                    break;
                }
                case HeapTag.INSTANCE_DUMP: {
                    final Instance instance = reader.readInstanceDump();
                    instances.put(instance.getObjectId(), instance);
                    break;
                }
                case HeapTag.OBJECT_ARRAY_DUMP: {
                    ObjectArray array = reader.readObjectArray();
                    objArrays.put(array.getObjectId(), array);
                    break;
                }
                case HeapTag.PRIMITIVE_ARRAY_DUMP: {
                    PrimitiveArray array = reader.readPrimitiveArray();
                    primitiveArrays.put(array.getObjectId(), array);
                    break;
                }
                default:
                    super.onHeapRecord(tag, reader);
            }
        }
    };

    public Map<ID, ClassDefinition> getClasses() {
        return classes;
    }

    public Map<ID, HprofString> getStrings() {
        return strings;
    }

    public Map<ID, ObjectArray> getObjectArrays() {
        return objArrays;
    }

    public Map<ID, PrimitiveArray> getPrimitiveArrays() {
        return primitiveArrays;
    }

    public Map<ID, Instance> getInstances() {
        return instances;
    }

    @Override
    public void onRecord(int tag, int timestamp, int length, @NonNull HprofReader reader) throws IOException {
        switch (tag) {
            case Tag.STRING: {
                HprofString string = reader.readStringRecord(length, timestamp);
                strings.put(string.getId(), string);
                break;
            }
            case Tag.LOAD_CLASS: {
                ClassDefinition cls = reader.readLoadClassRecord();
                classes.put(cls.getObjectId(), cls);
                break;
            }
            case Tag.HEAP_DUMP:
            case Tag.HEAP_DUMP_SEGMENT: {
                HeapDumpReader heapReader = new HeapDumpReader(reader.getInputStream(), length, heapDumpProcessor);
                while (heapReader.hasNext()) {
                    heapReader.next();
                }
                break;
            }
            default:
                super.onRecord(tag, timestamp, length, reader);
        }
    }
}
