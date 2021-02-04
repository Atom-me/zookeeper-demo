package com.atom.mallweb;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Atom
 */
public class Client {
    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new RetryOneTime(1000));
        curatorFramework.start();
        curatorFramework.blockUntilConnected();

        final ServiceDiscovery<Object> serviceDiscovery = ServiceDiscoveryBuilder
                .builder(Object.class)
                .client(curatorFramework)
                .basePath("/soa")
                .build();

        serviceDiscovery.start();

        final Collection<ServiceInstance<Object>> instances = serviceDiscovery.queryForInstances("product");
        instances.forEach(instance -> System.out.println(instance.toString()));

        RestTemplate rest = new RestTemplate();
        rest.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        final List<String> services = instances.stream().map(instance -> instance.getAddress() + ":" + instance.getPort()).collect(Collectors.toList());

        services.forEach(System.out::println);

        LoadBalance lb = new LoadBalance(services);

        final String body = rest.getForObject("http://" + lb.choose() + "/soa/product/" + "7", String.class);

        System.out.println(body);
//        final Response resp = new Gson().fromJson(body, Response.class);
//        System.out.println(resp.getCode());
//        System.out.println(resp.getMsg());
//        System.out.println(resp.getData());

//
//        for (ServiceInstance<Object> instance : instances) {
//            System.out.println(instance.getAddress());
//            System.out.println(instance.getPort());
//            final String body = rest.getForObject("http://" + instance.getAddress() + ":" + instance.getPort() + "/soa/products", String.class);
//            System.err.println(body);
//            final Response resp = new Gson().fromJson(body, Response.class);
//            System.out.println(resp.getCode());
//            System.out.println(resp.getMsg());
//            System.out.println(resp.getData());
//        }


    }
}
