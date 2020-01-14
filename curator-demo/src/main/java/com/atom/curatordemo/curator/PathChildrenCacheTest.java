package com.atom.curatordemo.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Before;
import org.junit.Test;

/**
 * 事件监听：
 * zookeeper原生支持通过注册watcher 来进行事件监听，但是使用不是特别方便，需要开发反复注册watcher。
 * <p>
 * curator引入了Cache来实现对zookeeper服务端事件对监听。
 * cache是curator中对事件监听的包装，其对事件的监听可以近似看作一个本地缓视图和远程zookeeper视图的对比过程。
 * 同时curator能够自动为开发人员处理反复注册监听。
 * cache分为两类监听类型：
 * 1。节点监听
 * NodeCache 可以监听到节点内容到变
 * NodeCache无法监听子节点变更
 * 2。子节点监听
 * PathChildrenCache
 *
 * @author Atom
 */
public class PathChildrenCacheTest {

    static final String CONNECT_STRING = "localhost:2181";
    static final int SESSION_TIMEOUT = 30_000;
    static CuratorFramework curatorFramework = null;

    @Before
    public void before() {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);

        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_STRING)
                .sessionTimeoutMs(SESSION_TIMEOUT)
                .retryPolicy(retryPolicy)
                .build();

        curatorFramework.start();
    }

    /**
     * pathChildrenCache 用于监听指定zookeeper数据节点的子节点变化情况。
     * <p>
     * PathChildrenCache 构造函数 boolean cacheData 是否缓冲节点内容数据。
     * <p>
     * PathChildrenCache 只能监控一级子节点，无法对二级子节点进行事件监听
     *
     * @throws Exception
     */
    @Test
    public void testPathChildrenCache() throws Exception {
        final PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, "/super", true);

        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

        pathChildrenCache.getListenable()
                .addListener(new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                        switch (event.getType()) {
                            case CHILD_ADDED:
                                System.out.println("添加子节点===》" + event.getData().getPath());
                                System.out.println("添加子节点的数据为===》" + new String(event.getData().getData()));
                                break;
                            case CHILD_UPDATED:
                                System.out.println("更新节点名称====》" + event.getData().getPath());
                                System.out.println("更新节点数据为====》" + new String(event.getData().getData()));
                                break;
                            case CHILD_REMOVED:
                                System.out.println("删除节点====》" + event.getData().getPath());
                                System.out.println("删除节点数据为====》" + new String(event.getData().getData()));
                                break;
                            default:
                                break;

                        }
                    }
                });

        curatorFramework.create()
                .withMode(CreateMode.PERSISTENT)
                .forPath("/super");
        Thread.sleep(1000);

        curatorFramework.create()
                .withMode(CreateMode.PERSISTENT)
                .forPath("/super/aaa", "aaa11".getBytes());
        Thread.sleep(1000);

        curatorFramework.setData()
                .forPath("/super/aaa", "aaa222".getBytes());
        Thread.sleep(1000);

        curatorFramework.setData().forPath("/super", "123".getBytes());
        Thread.sleep(1000);


        curatorFramework.create()
                .withMode(CreateMode.PERSISTENT)
                .forPath("/super/aaa/bbb", "bbb111".getBytes());
        Thread.sleep(1000);

        curatorFramework.delete()
                .guaranteed()
                .deletingChildrenIfNeeded()
                .forPath("/super");

        Thread.sleep(Integer.MAX_VALUE);
    }

}
