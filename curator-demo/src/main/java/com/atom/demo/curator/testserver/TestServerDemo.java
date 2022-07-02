package com.atom.demo.curator.testserver;

import org.apache.curator.test.TestingServer;

import java.io.File;

/**
 * Curator 提供了一种启动简易 zookeeper 服务的方法， TestingServer . TestServer允许开发人员非常方便的启动一个标准 zookeeper 服务器。
 * 方便开发人员进行开发和测试。
 * TestingServer 允许开发人员自定义zookeeper服务器对外服务的端口和dataDir路径，
 * 如果没有指定DataDir，那么 curator默认会在系统的临时目录中创建一个临时目录作为数据存储目录。
 *
 * @author Atom
 */
public class TestServerDemo {
    public static void main(String[] args) throws Exception {
        /**
         * TestingServer 允许开发人员自定义zookeeper服务器对外服务的端口和dataDir路径，
         *  如果没有指定DataDir，那么 curator默认会在系统的临时目录中创建一个临时目录作为数据存储目录。
         */
        TestingServer server = new TestingServer(2182, new File("/Users/atom/zk_test_data"));
        server.start();
    }
}
