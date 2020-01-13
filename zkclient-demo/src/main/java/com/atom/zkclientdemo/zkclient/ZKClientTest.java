package com.atom.zkclientdemo.zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author Atom
 */
public class ZKClientTest {

    private static final String CONNECT_STRING = "localhost:2181";
    private ZkClient zkClient;

    @Before
    public void setUp() {
        zkClient = new ZkClient(new ZkConnection(CONNECT_STRING));
    }

    /**
     * zkclient 支持递归创建节点 createPersistent
     */
    @Test
    public void testRecursiveCreate() {
        zkClient.createPersistent("/temp/a/b/c", true);
        assertThat(zkClient.exists("/temp/a/b/c"), equalTo(true));
        final boolean b = zkClient.deleteRecursive("/temp/a/b/c");
        assertThat(b, equalTo(true));
    }

    /**
     * zkclient创建临时节点
     */
    @Test
    public void testCreateEphemeral() {
        zkClient.createEphemeral("/testephemeral", "aaa");
        assertThat(zkClient.exists("/testephemeral"), equalTo(true));
        zkClient.delete("/testephemeral");
    }
}
