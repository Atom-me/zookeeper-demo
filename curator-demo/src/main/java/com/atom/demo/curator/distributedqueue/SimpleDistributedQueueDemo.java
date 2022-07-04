package com.atom.demo.curator.distributedqueue;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.queue.SimpleDistributedQueue;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 简单的分布式队列。版本使用2.8.0 其他版本不兼容
 *
 * <dependency>
 * <groupId>org.apache.curator</groupId>
 * <artifactId>curator-recipes</artifactId>
 * <version>2.8.0</version>
 * </dependency>
 *
 * @author Atom
 */
public class SimpleDistributedQueueDemo {

    private static String queuePath = "/zk_queue/simple_distributed_queue";

    private static CuratorFramework zkClient = CuratorFrameworkFactory.builder()
            .connectString("localhost:2181")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();


    public static void main(String[] args) throws InterruptedException {
        zkClient.start();
        //创建分布式队列
        SimpleDistributedQueue distributedQueue = new SimpleDistributedQueue(zkClient, queuePath);

        //生产者线程池
        ExecutorService executorService = Executors.newCachedThreadPool();

        //生产
        BiConsumer<SimpleDistributedQueue, String> putConsumer = (queue, item) -> {
            try {
                Callable call = () -> {
                    try {
                        int sleepSeconds = (int) (Math.random() * 10);
                        System.err.println("sleepSeconds:" + sleepSeconds + "  item:" + item);

                        TimeUnit.SECONDS.sleep(sleepSeconds);
                        //向队尾添加数据
                        queue.offer(item.getBytes());
                        System.err.println(Thread.currentThread() + "  put : " + item + "    time:::" + new Date());
                    } catch (Exception e) {
                    }
                    return true;
                };
                executorService.submit(call);
            } catch (Exception e) {
            }
        };


        //消费
        Consumer<SimpleDistributedQueue> getConsumer = (queue) -> {
            try {
                Callable call = () -> {
                    try {
                        while (true) {
                            //从队首取出数据
                            byte[] dataByte = queue.take();
                            String data = new String(dataByte);
                            if (StringUtils.isBlank(data)) {
                                break;
                            }
                            System.out.println(Thread.currentThread() + "  get : " + data);
                        }
                    } catch (Exception e) {
                    }
                    return true;
                };
                executorService.submit(call);
            } catch (Exception e) {
            }
        };


        //分布式队列测试(5个线程生产)
        System.out.println("5个并发线程生产，测试分布式队列");
        //5个生产线程
        for (int i = 0; i < 5; i++) {
            putConsumer.accept(distributedQueue, "item" + i);
        }

        //分布式队列测试(2个线程消费)
        System.out.println("2个并发线程消费,测试分布式队列");
        //2个消费线程
        for (int i = 0; i < 2; i++) {
            getConsumer.accept(distributedQueue);
        }

        executorService.shutdown();
        TimeUnit.SECONDS.sleep(20);
        zkClient.close();

    }
}
