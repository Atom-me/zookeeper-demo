package com.atom.zksampledemo.zktest;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Atom
 */
public class TestZK {

    @Test
    public void ls() throws Exception {
        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 5000, null);
        final List<String> children = zooKeeper.getChildren("/", null);
        children.forEach(System.out::println);
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
        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 5000, null);
        final List<String> children = zooKeeper.getChildren(path, null);
        if (children == null || children.isEmpty()) {
            return;
        }
        for (String child : children) {
            if (path.equals("/")) {
                System.out.println(path + child);
                ls(path + child);
            } else {
                System.out.println(path + "/" + child);
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
        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 5000, null);
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

        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 5000, null);
        final String s = zooKeeper.create("/b", "abc".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(s);
    }

    /**
     * 观察者模式 Watcher 监控节点变更，只会触发一次
     *
     * @throws Exception
     */
    @Test
    public void testWatch() throws Exception {
        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 5000, null);
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

}
