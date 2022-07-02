package com.atom.demo.curator.mutex;

import java.util.concurrent.CountDownLatch;

/**
 * 典型的时间戳生成的并发问题
 *
 * @author Atom
 */
public class Recipes_NoLock {

    public static void main(String[] args) {


        //CountDownLatch 提供了一个构造方法，你必须指定其初始值，还指定了 countDown 方法，
        // 这个方法的作用主要用来减小计数器的值，当计数器变为 0 时，在 CountDownLatch 上 await 的线程就会被唤醒
        final CountDownLatch down = new CountDownLatch(1);

        for (int i = 0; i < 50; i++) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //所有的子线程启动之后到这里都进入等待状态。
                        down.await();
                    } catch (Exception e) {
                        //ignore
                    }
                    try {
                        System.err.println("线程" + Thread.currentThread().getName() + " 开始处理业务===========================");
                    } finally {
                        System.err.println("线程" + Thread.currentThread().getName() + " 处理业务结束===========================\n");
                    }
                }
            }).start();
        }

        down.countDown();

    }
}
