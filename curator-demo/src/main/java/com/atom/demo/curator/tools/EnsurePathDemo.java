package com.atom.demo.curator.tools;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.EnsurePath;

/**
 * EnsurePath 已过时，不推荐使用。
 * Deprecated
 * Since 2.9.0 - Prefer CuratorFramework.create().creatingParentContainersIfNeeded() or CuratorFramework.exists().creatingParentContainersIfNeeded()
 * <p>
 * EnsurePath 提供了一种能够确保数据节点存在的机制，
 * 它采用静默的节点创建方式，其内部实现就是试图创建指定节点，如果节点已经存在，那么就不进行任何操作，
 * 也不对外抛出异常，否则正常创建数据节点。
 *
 * @author Atom
 */
public class EnsurePathDemo {

    private static String path = "/zk-book/c1";
    private static CuratorFramework zkClient = CuratorFrameworkFactory.builder()
            .connectString("localhost:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();


    public static void main(String[] args) throws Exception {
        zkClient.start();
        zkClient.usingNamespace("zk-book");

        /**
         *  EnsurePath 已过时，不推荐使用
         */
        EnsurePath ensurePath = new EnsurePath(path);
        // first time syncs and creates if needed
        ensurePath.ensure(zkClient.getZookeeperClient());
        // subsequent times are NOPs
        ensurePath.ensure(zkClient.getZookeeperClient());


        /**
         * Deprecated
         * Since 2.9.0 - prefer
         * CreateBuilder.creatingParentContainersIfNeeded(),
         * ExistsBuilder.creatingParentContainersIfNeeded()
         * or createContainers(String)
         */
        EnsurePath ensurePath1 = zkClient.newNamespaceAwareEnsurePath("/c1");
        ensurePath1.ensure(zkClient.getZookeeperClient());


    }
}
