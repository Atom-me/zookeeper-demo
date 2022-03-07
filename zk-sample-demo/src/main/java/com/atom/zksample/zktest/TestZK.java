package com.atom.zksample.zktest;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Atom
 */
public class TestZK {

    private static final String CONNECT_STRING = "192.168.56.101:2181,192.168.56.102:2181,192.168.56.103:2181";
    private static final int SESSION_TIMEOUT = 5000;

    private ZooKeeper zooKeeper;

    @Before
    public void before() throws IOException {
        zooKeeper = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT, null);
    }

    @After
    public void after() throws InterruptedException {
        zooKeeper.close();
    }

    /**
     * getChildren 方法只能列出指定路径的一层节点
     *
     * @throws Exception
     */
    @Test
    public void ls() throws Exception {
        final List<String> children = zooKeeper.getChildren("/", null);
        children.forEach(System.err::println);
    }

    @Test
    public void lsAll() throws Exception {
        ls("/");
    }

    /**
     * 递归列出指定path下的所有节点
     *
     * @param path
     * @throws Exception
     */
    public void ls(String path) throws Exception {
        final List<String> children = zooKeeper.getChildren(path, null);
        if (children == null || children.isEmpty()) {
            return;
        }
        for (String child : children) {
            if (path.equals("/")) {
                System.err.println(path + child);
                ls(path + child);
            } else {
                System.err.println(path + "/" + child);
                ls(path + "/" + child);
            }
        }
    }

    /**
     * 修改数据 需要设置version数据（乐观锁机制），版本不一致会修改失败
     *
     * @throws Exception
     */
    @Test
    public void testSetData() throws Exception {
        final Stat stat = zooKeeper.setData("/a", "ddd".getBytes(), 0);
        System.out.println(stat);
    }

    /**
     * 创建节点必须指定ACL
     *
     * @throws Exception
     */
    @Test
    public void testCreateNode() throws Exception {
        /**
         * This is a completely open ACL .
         */
//        final ArrayList<ACL> OPEN_ACL_UNSAFE = new ArrayList<ACL>(Collections.singletonList(new ACL(ZooDefs.Perms.ALL, ANYONE_ID_UNSAFE)));

        final String s = zooKeeper.create("/b", "abc".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(s);
    }

    /**
     * 观察者模式 Watcher 监控节点变更，只会触发一次.
     * <p>
     * Watcher 是观察者对象，用于回调的。
     *
     * @throws Exception
     */
    @Test
    public void testWatch() throws Exception {
        Stat stat = new Stat();
        final byte[] data = zooKeeper.getData("/b", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.err.println("数据被修改了。。。。" + event.toString());
            }
        }, stat);

        System.err.println(new String(data));

        TimeUnit.MINUTES.sleep(2);
    }


    /**
     * 观察者模式 Watcher 监控节点变更，重复注册（持续监控）
     *
     * @throws Exception
     */
    @Test
    public void testWatch2() throws Exception {
        Stat stat = new Stat();

        final Watcher watcher = new Watcher() {
            // 回调
            @Override
            public void process(WatchedEvent event) {
                System.err.println("数据被修改了。。。。" + event.toString());
                try {
                    // 再次注册，就可以再次接到通知，把当前watcher传进来
                    zooKeeper.getData("/b", this, stat);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        final byte[] data = zooKeeper.getData("/b", watcher, stat);

        System.err.println(new String(data));

        TimeUnit.MINUTES.sleep(2);
    }
}
