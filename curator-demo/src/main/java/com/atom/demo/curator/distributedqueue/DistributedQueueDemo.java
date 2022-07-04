package com.atom.demo.curator.distributedqueue;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.recipes.queue.QueueSerializer;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * 一个基于ZK的分布式队列实现。
 * 放入的消息可以保证顺序（基于zk的有序持久化节点）。
 * 对于单个消费者来说，队列是一个FIFO（先进先出）的方式。 如果需要控制顺序，可以为消费者指定一个LeaderSelector，来定制消费策略。
 * <p>
 * 分布式队列。版本使用2.8.0 其他版本不兼容
 * 分布式队列 DistributedQueue 和消息队列相似，需要定义监听器和消息序列化方式。
 * <dependency>
 * <groupId>org.apache.curator</groupId>
 * <artifactId>curator-recipes</artifactId>
 * <version>2.8.0</version>
 * </dependency>
 *
 * @author Atom
 */
public class DistributedQueueDemo {

    private static String queuePath = "/zk_queue/distributed_queue";

    private static CuratorFramework zkClient = CuratorFrameworkFactory.builder()
            .connectString("localhost:2181")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();


    public static void main(String[] args) throws Exception {
        zkClient.start();

        //消费监听器,即队列消费者
        QueueConsumer<String> queueConsumer = new QueueConsumer<String>() {
            @Override
            public void consumeMessage(String message) throws Exception {
                System.out.println("consume message: " + message);
            }

            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {

            }
        };

        //序列化
        QueueSerializer<String> queueSerializer = new QueueSerializer<String>() {
            @Override
            public byte[] serialize(String item) {
                return item.getBytes();
            }

            @Override
            public String deserialize(byte[] bytes) {
                return new String(bytes);
            }
        };


        //创建分布式队列
        QueueBuilder<String> queueBuilder = QueueBuilder.builder(zkClient, queueConsumer, queueSerializer, queuePath);
        DistributedQueue<String> stringDistributedQueue = queueBuilder.buildQueue();
        //队列开始使用之前需要调用start()方法。当用完之后需要调用close()。
        stringDistributedQueue.start();

        //生产者线程池
        ExecutorService executorService = Executors.newCachedThreadPool();

        //生产
        BiConsumer<DistributedQueue<String>, String> putConsumer = (queue, item) -> {
            try {
                Callable call = () -> {
                    try {
                        int sleepSeconds = (int) (Math.random() * 10);
                        System.err.println("sleepSeconds:" + sleepSeconds + "  item:" + item);
                        TimeUnit.SECONDS.sleep(sleepSeconds);

                        //向队尾添加数据
                        queue.put(item);
                        System.err.println(Thread.currentThread() + "  put : " + item + "    time:::" + new Date());
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
            putConsumer.accept(stringDistributedQueue, "item" + i);
        }


        executorService.shutdown();
        TimeUnit.SECONDS.sleep(20);
        stringDistributedQueue.close();
        zkClient.close();

    }
}
