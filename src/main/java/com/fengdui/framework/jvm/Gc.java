package com.fengdui.framwork.jvm;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

public class Gc {

    /**
     * Java读取GC的耗时和次数
     * @param args
     */
    public static void main(String[] args) {
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            long count = gc.getCollectionCount();
            long time = gc.getCollectionTime();
            String name = gc.getName();
            System.out.println(String.format("%s: %s times %s ms", name, count, time));
        }
    }
}
