package com.atom.mallproduct;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 向zookeeper中注册本服务
 *
 * @author Atom
 */
@Component
public class ServiceRegister implements ApplicationRunner {
    @Value("${zookeeper.address}")
    private String zkAddress;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zkAddress, new RetryOneTime(1000));
        curatorFramework.start();
        curatorFramework.blockUntilConnected();

        final ServiceInstance<Object> instance = ServiceInstance.builder()
                .name("product")
                .address("192.168.56.1")
                .port(8080)
                .build();

        final ServiceInstance<Object> instance2 = ServiceInstance.builder()
                .name("product")
                .address("192.168.56.2")
                .port(8080)
                .build();


        final ServiceDiscovery<Object> serviceDiscovery = ServiceDiscoveryBuilder
                .builder(Object.class)
                .client(curatorFramework)
                .basePath("/soa")
                .build();

        //模拟启动多实例部署，注册多个服务地址
        serviceDiscovery.registerService(instance);
//        serviceDiscovery.registerService(instance2);
        serviceDiscovery.start();

        System.out.println("service register ok .....");


    }
}
