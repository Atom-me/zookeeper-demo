package com.atom.demo.curator.tools;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.ZooKeeper;

/**
 * Curator 提供的常用工具类：ZKPaths
 * ZKPaths 提供了一些简单的API来构建ZNode路径，递归创建和删除，查询节点等。
 *
 * @author Atom
 */
public class ZKPaths_Sample {

    private static String path = "/curator_zkpath_sample";
    private static CuratorFramework zkClient = CuratorFrameworkFactory.builder()
            .connectString("localhost:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    public static void main(String[] args) throws Exception {
        zkClient.start();
        ZooKeeper zooKeeper = zkClient.getZookeeperClient().getZooKeeper();

        //将名称空间应用于给定的路径， 输出/curator_zkpath_sample/sub
        System.err.println(ZKPaths.fixForNamespace(path, "/sub"));

        //给定父路径和子节点，创建一个组合的完整路径，输出/curator_zkpath_sample/sub1
        String sub = ZKPaths.makePath(path, "sub1");
        System.err.println(sub);

        //给定完整路径，返回节点名。即。"/cluster/sub1"会返回"sub1"
        String nodeFromPath = ZKPaths.getNodeFromPath("/curator_zkpath_sample/sub2");
        System.err.println(nodeFromPath);


        //将path和node 拆开
        ZKPaths.PathAndNode pathAndNode = ZKPaths.getPathAndNode("/curator_zkpath_sample/sub1");
        System.err.println(pathAndNode.getPath());
        System.err.println(pathAndNode.getNode());


        String dir1 = path + "/child1";
        String dir2 = path + "/child2";
        ZKPaths.mkdirs(zooKeeper, dir1);
        ZKPaths.mkdirs(zooKeeper, dir2);
        System.err.println(ZKPaths.getSortedChildren(zooKeeper, path));

        //删除所有子节点，包含自己
        ZKPaths.deleteChildren(zooKeeper, path, true);

    }
}
