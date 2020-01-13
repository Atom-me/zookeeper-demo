package com.atom.zksampledemo.zktest;

import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

import java.util.List;

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
}
