package com.atom.curatordemo.curator;

/**
 * 事件监听：
 * zookeeper原生支持通过注册watcher 来进行事件监听，但是使用不是特别方便，需要开发反复注册watcher。
 * <p>
 * curator引入了Cache来实现对zookeeper服务端事件对监听。
 * cache是curator中对事件监听的包装，其对事件的监听可以近似看作一个本地缓视图和远程zookeeper视图的对比过程。
 * 同时curator能够自动为开发人员处理反复注册监听。
 * cache分为两类监听类型：
 * 1。节点监听
 * NodeCache 可以监听到节点内容到变
 * NodeCache无法监听子节点变更
 * 2。子节点监听
 * PathChildrenCache
 *
 * @author Atom
 */
public class PathChildrenCacheTest {
}
