package com.atom.curatordemo.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
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
public class NodeCacheTest {

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
     * NodeCache 可以监听到节点内容到变更，如果删除这个节点，也能会触发监听器，但是拿不到数据。
     * NodeCache无法监听子节点变更
     *
     * @throws Exception
     */
    @Test
    public void testNodeCache() throws Exception {
        final NodeCache nodeCache = new NodeCache(curatorFramework, "/super", false);
        nodeCache.start(true);
        nodeCache.getListenable()
                .addListener(new NodeCacheListener() {
                    @Override
                    public void nodeChanged() throws Exception {
                        System.out.println("监听到节点变更。。。。。");
                        System.out.println("===============");
                        System.out.println("path=====》" + nodeCache.getCurrentData().getPath());
                        System.out.println("data======》" + new String(nodeCache.getCurrentData().getData()));
                        System.out.println("stat======》" + nodeCache.getCurrentData().getStat());
                    }
                });

        Thread.sleep(1000);
        curatorFramework.create().forPath("/super", "123".getBytes());

        Thread.sleep(Integer.MAX_VALUE);
    }

}
