package com.experience.greatdumper.provider;

import com.experience.greatdumper.hprofdata.MemoryHprof;
import com.experience.greatdumper.hprofdata.base.ClassDefinition;
import com.google.firebase.crashlytics.buildtools.reloc.javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassProvider extends BaseProvider {

    private final MemoryHprof data;
    private final Map<ClassDefinition, String> classNames = new HashMap<ClassDefinition, String>();

    public ClassProvider(@Nonnull MemoryHprof data) {
        this.data = data;
        for (ClassDefinition cls : data.classes.values()) {
            classNames.put(cls, data.strings.get(cls.getNameStringId()).getValue());
        }
        setStatus(ProviderStatus.LOADED);
    }

    public String getClassName(@Nonnull ClassDefinition cls) {
        return classNames.get(cls);
    }

    public int getInstanceSizeForClass(@Nonnull ClassDefinition cls) {
        int size = 0;
        //noinspection ConstantConditions
        while (cls != null) {
            size += cls.getInstanceSize();
            cls = data.classes.get(cls.getSuperClassObjectId());
        }
        return size;
    }

    public List<ClassDefinition> getClassesMatchingQuery(@Nonnull String query) {
        List<ClassDefinition> result = new ArrayList<ClassDefinition>();
        for (ClassDefinition cls : data.classes.values()) {
            String className = data.strings.get(cls.getNameStringId()).getValue();
            if (className.contains(query)) {
                result.add(cls);
            }
        }
        return result;
    }
}