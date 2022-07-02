package com.atom.demo.curator.mutex;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.CountDownLatch;

/**
 * 使用curator实现分布式锁功能
 *
 * @author Atom
 */
public class Recipes_Lock {
    private static String lock_path = "/curator_recipes_lock_path";

    private static CuratorFramework zkClient = CuratorFrameworkFactory.builder()
            .connectString("localhost:2181")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    public static void main(String[] args) {
        zkClient.start();

        //声明锁对象，即 创建ZK临时顺序节点 , InterProcessMutex
        final InterProcessMutex lock = new InterProcessMutex(zkClient, lock_path);

        //CountDownLatch 提供了一个构造方法，你必须指定其初始值，还指定了 countDown 方法，
        // 这个方法的作用主要用来减小计数器的值，当计数器变为 0 时，在 CountDownLatch 上 await 的线程就会被唤醒
        final CountDownLatch down = new CountDownLatch(1);

        for (int i = 0; i < 500; i++) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //所有的子线程启动之后到这里都进入等待状态。
                        down.await();
                    } catch (Exception e) {
                    }

                    try {
                        //获取锁
                        lock.acquire();
                        //处理业务：生成订单号
                        System.err.println("线程" + Thread.currentThread().getName() + " 开始处理业务===========================");
                    } catch (Exception e) {
                        //ignore
                    } finally {
                        System.err.println("线程" + Thread.currentThread().getName() + " 处理任务结束===========================\n");
                        try {
                            //释放锁
                            lock.release();
                        } catch (Exception e) {
                            //ignore
                        }
                    }


                }
            }).start();
        }
        down.countDown();

    }
}
