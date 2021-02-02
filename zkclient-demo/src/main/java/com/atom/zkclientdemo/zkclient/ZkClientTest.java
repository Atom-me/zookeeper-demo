package com.atom.zkclientdemo.zkclient;

import com.atom.zkclientdemo.serializer.ZkStringSerializer;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author Atom
 */
public class ZkClientTest {

    private static final String CONNECT_STRING = "localhost:2181";
    private ZkClient zkClient;

    @Before
    public void setUp() {
        zkClient = new ZkClient(new ZkConnection(CONNECT_STRING), 30_000, new ZkStringSerializer());
    }

    @After
    public void after() {
        zkClient.close();
    }

    /**
     * zkClient 支持递归创建节点 createPersistent
     */
    @Test
    public void testRecursiveCreate() {
        zkClient.createPersistent("/temp/a/b/c", true);
        assertThat(zkClient.exists("/temp/a/b/c"), equalTo(true));
        final boolean b = zkClient.deleteRecursive("/temp/a/b/c");
        assertThat(b, equalTo(true));
    }

    /**
     * zkClient 创建临时节点,临时节点，session过期，创建连接的时候有一个过期时间，，不是断开，跟断开不是一个意思，自动删除
     */
    @Test
    public void testCreateEphemeral() {
        zkClient.createEphemeral("/testephemeral", "aaa");
        assertThat(zkClient.exists("/testephemeral"), equalTo(true));
    }

    /**
     * zkClient 创建持久节点
     */
    @Test
    public void testCreatePersistent() {
        zkClient.createPersistent("/persistent", "aaa");
        assertThat(zkClient.exists("/persistent"), equalTo(true));
        final Object o = zkClient.readData("/persistent");
        assertThat(o.toString(), equalTo("aaa"));
    }


    /**
     * zkClient 提供的API没有了watcher注册的功能。
     * zkClient 引入了listener的概念，客户端可以通过注册相关的事件监听对zookeeper服务端事件对订阅，
     * subscribeChildChanges 这个接口只对节点列表变更监听，不对节点内容变更监听。
     *
     * @throws InterruptedException
     */
    @Test
    public void testSubscribeChildChanges() throws InterruptedException {
        zkClient.createPersistent("/super", "1111");
        zkClient.subscribeChildChanges("/super", new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.err.println("parentPath====>" + parentPath);
                currentChilds.forEach(System.err::println);
            }
        });

        TimeUnit.MINUTES.sleep(5);

    }


    /**
     * zkClient 提供的API没有了watcher注册的功能。
     * zkClient 引入了listener的概念，客户端可以通过注册相关的事件监听对zookeeper服务端事件对订阅，
     * subscribeDataChanges 这个接口只对节点内容变更和节点删除监听，不对节点列表变更监听。
     *
     * @throws InterruptedException
     */
    @Test
    public void testSubscribeDataChanges() throws InterruptedException {
        zkClient.createPersistent("/super", "1111");
        zkClient.subscribeDataChanges("/super", new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.err.println("datapath======" + dataPath);
                System.err.println("data========" + data);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.err.println("handleDataDeleted=====" + dataPath);
            }
        });
        TimeUnit.MINUTES.sleep(5);
    }

}
