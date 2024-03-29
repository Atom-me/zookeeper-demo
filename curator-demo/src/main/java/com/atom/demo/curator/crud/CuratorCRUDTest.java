package com.atom.demo.curator.crud;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author Atom
 */
public class CuratorCRUDTest {

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
     * curator 创建节点，删除节点
     *
     * @throws Exception
     */
    @Test
    public void testCreateAndDelete() throws Exception {

        curatorFramework.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath("/curatorTest", "test".getBytes());

        assertThat(curatorFramework.getData().forPath("/curatorTest"), equalTo("test".getBytes()));

        curatorFramework.delete()
                .guaranteed()
                .deletingChildrenIfNeeded()
                .forPath("/curatorTest");
    }


    /**
     * curator 读取节点数据
     *
     * @throws Exception
     */
    @Test
    public void testRead() throws Exception {
        final byte[] bytes = curatorFramework.getData()
                .forPath("/persistent");
        String result = new String(bytes);
        System.out.println(result);
    }

    /**
     * curator 修改节点数据
     *
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {
        curatorFramework.setData()
                .forPath("/persistent", "bbb".getBytes());
        assertThat(curatorFramework.getData().forPath("/persistent"), equalTo("bbb".getBytes()));
    }

    /**
     * curator递归删除节点
     *
     * @throws Exception
     */
    @Test
    public void testRecursiveDelete() throws Exception {
        curatorFramework.delete()
                .guaranteed()
                .deletingChildrenIfNeeded()
                .forPath("/temp");
    }
}
