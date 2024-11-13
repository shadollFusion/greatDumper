package com.experience.greatdumper.hprofutils;

import android.util.Log;

import com.experience.greatdumper.hprofdata.MemoryHprof;
import com.experience.greatdumper.hprofdata.base.ClassDefinition;
import com.experience.greatdumper.hprofdata.base.HprofString;
import com.experience.greatdumper.hprofdata.base.ID;
import com.experience.greatdumper.hprofdata.base.Instance;
import com.experience.greatdumper.provider.InstanceProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HprofViewer {
    private static final String TAG = "HprofViewer";
    public static Map<String, Integer> allocationNum = new HashMap<>();
    public static void readHprofFile(File hprofFile) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(hprofFile));
        ViewDataProcessor processor = new ViewDataProcessor();
        HprofReader reader = new HprofReader(in, processor);
        int count = 0;
        while (reader.hasNext()) {
            count++;
            reader.next();
        }
        MemoryHprof data = new MemoryHprof(processor.getClasses(), processor.getStrings(), processor.getInstances(),
                processor.getObjectArrays(), processor.getPrimitiveArrays());
        InstanceProvider instanceProvider = new InstanceProvider(data);
        for (ClassDefinition cls : data.classes.values()) {
            int size = cls.getInstanceSize();
            String clsName = data.strings.get(cls.getNameStringId()).getValue();
            int instanceNum = instanceProvider.getInstanceCountOfClass(cls);
            Log.d(TAG, "size: " + String.valueOf(size) + " clsName: " + clsName + " instanceNum: " + String.valueOf(instanceNum));
        }

        for (Instance instance : data.instances.values()) {
            int instanceSize = instance.getInstanceFieldData().length;
            ID clsId = instance.getClassId();
            ClassDefinition cls = data.classes.get(clsId);
            String clsName = data.strings.get(cls.getNameStringId()).getValue();
            ID objId = instance.getObjectId();
            allocationNum.put(clsName, allocationNum.getOrDefault(clsName, 0) + 1);
        }
        Map<ID, ClassDefinition> viewClasses = filterViewClasses(data);
        System.out.println("Found " + viewClasses.size() + " View classes");

        // Filter out the classinfo dumps of the View classes
        List<Instance> viewInstances = filterViewInstances(data, viewClasses);
        System.out.println("Found " + viewInstances.size() + " classinfo of View classes");

        // Find the View roots (Decor views), there should be at least one
//        ClassDefinition decorClass = findDecorClass(viewClasses, data);
//        List<Instance> viewRoots = findViewRoots(viewInstances, decorClass);
//        System.out.println("Found " + viewRoots.size() + " roots classinfo of " + data.strings.get(decorClass.getNameStringId()).getValue());
//        Log.d(TAG, data.classes.size());
    }
    private static Map<ID, ClassDefinition> filterViewClasses(MemoryHprof data) {
        Map<ID, ClassDefinition> viewClasses = new HashMap<ID, ClassDefinition>();
        for (ClassDefinition cls : data.classes.values()) {
            if (isView(cls, data)) {
                viewClasses.put(cls.getObjectId(), cls);
            }
        }
        return viewClasses;
    }
    private static List<Instance> filterViewInstances(MemoryHprof data, Map<ID, ClassDefinition> viewClasses) {
        List<Instance> viewInstances = new ArrayList<Instance>();
        for (Instance instance : data.instances.values()) {
            if (viewClasses.containsKey(instance.getClassId())) {
                viewInstances.add(instance);
            }
        }
        return viewInstances;
    }
    private static ClassDefinition findDecorClass(Map<ID, ClassDefinition> viewClasses, MemoryHprof data) {
        for (ClassDefinition cls : viewClasses.values()) {
            HprofString clsName = data.strings.get(cls.getNameStringId());
            if (clsName.getValue().endsWith("$DecorView")) {
                return cls;
            }
        }
        throw new IllegalStateException("Dump contained no decor views!");
    }
    private static List<Instance> findViewRoots(List<Instance> viewInstances, ClassDefinition decorClass) {
        List<Instance> roots = new ArrayList<Instance>();
        for (Instance instance : viewInstances) {
            if (instance.getClassId().equals(decorClass.getObjectId())) {
                roots.add(instance);
            }
        }
        return roots;
    }
    private static boolean isView(ClassDefinition cls, MemoryHprof data) {
        while (cls != null) {
            HprofString clsName = data.strings.get(cls.getNameStringId());
            if (clsName == null) {
                System.err.println("Missing string id: " + cls.getNameStringId());
                return false;
            }
            if ("android.view.View".equals(clsName.getValue())) {
                return true;
            }
            cls = data.classes.get(cls.getSuperClassObjectId());
        }
        return false;
    }
}
