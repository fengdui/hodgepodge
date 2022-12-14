package com.hodgepodge.framework.id;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class IdUtils {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Long start = System.currentTimeMillis();
        Set<String> set = new HashSet<>();
        for (int i = 0; i < 100000; i++) {
            executor.submit(() -> {
                String s = String.valueOf(System.currentTimeMillis()) + ThreadLocalRandom.current().nextInt(1000000, 10000000);
                System.out.println(s);
                set.add(s);
            });
        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);


        System.out.println(set.size());
        System.out.println("cost:" + (System.currentTimeMillis() - start));
    }
}
