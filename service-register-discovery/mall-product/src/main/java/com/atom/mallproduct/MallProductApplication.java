package com.atom.mallproduct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MallProductApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(MallProductApplication.class, args);
//        final ProductMapper productMapper = context.getBean(ProductMapper.class);
//        Product product = new Product();
//        product.setPname("JAVA从入门到精通");
//        product.setPrice(68d);
//        product.setType("书籍");
//        productMapper.addProduct(product);
//        context.close();
    }

}
