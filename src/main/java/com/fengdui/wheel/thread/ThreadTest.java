package com.fengdui.wheel.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author fengdui1
 * @since 2020/6/18 6:47 下午
 */
public class ThreadTest {
    private static final ExecutorService pool = new ThreadPoolExecutor(10, 20, 10, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1000));
    public  static  void main(String[] args) {
        Thread t1 =  new MyCommon();
        Thread t2 =  new Thread( new MyDaemon());
        t1.setDaemon(true);
        t2.setDaemon( true); //设置为守护线程
        t2.start();
        t1.start();

    }

   static class MyCommon  extends Thread {
        public  void run() {
            for ( int i = 0; i < 50; i++) {
                System.out.println( "线程1第" + i +  "次执行！");
                try {
                    Thread.sleep(7);
                }  catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class MyDaemon  implements Runnable {
        public void run() {
            for (long i = 0; i < 9999999L; i++) {
                System.out.println("后台线程第" + i + "次执行！");
                try {
                    Thread.sleep(7);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
