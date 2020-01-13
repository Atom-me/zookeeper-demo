package com.atom.curatordemo.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Atom
 */
public class CuratorTest {

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
     * curator 创建节点
     *
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {

        curatorFramework.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath("/curatorTest", "test".getBytes());
    }
}