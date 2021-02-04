package com.atom.mallproduct.controller;

import com.atom.mallproduct.bean.Product;
import com.atom.mallproduct.mapper.ProductMapper;
import com.atom.mallproduct.web.Response;
import com.atom.mallproduct.web.SubResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Atom
 */
@RestController
public class ProductController {

    @Resource
    private ProductMapper productMapper;

    @PostMapping("/soa/product/add")
    public Object add(Product product) {
        Integer res = productMapper.addProduct(product);
        return res == 1 ? new Response("200", "OK") : new Response("500", "fail");
    }

    @PutMapping("/soa/product/update")
    public Object update(Product product) {
        Integer res = productMapper.update(product);
        return res == 1 ? new Response("200", "OK") : new Response("500", "fail");
    }

    @GetMapping("/soa/product/{id}")
    public Object get(@PathVariable("id") Integer id) {
        final Product product = productMapper.getById(id);
        return new Response("200", "ok", product);
    }

    @DeleteMapping("/soa/product/{id}")
    public Object delete(@PathVariable("id") Integer id) {
        Integer res = productMapper.deleteById(id);
        return res == 1 ? new Response("200", "OK") : new Response("500", "fail");
    }

    @GetMapping("/soa/products")
    public Object list() {
        return new Response("200", "ok", productMapper.queryBylists());
    }


    @PostMapping("/soa/feign/products")
    public Response<Product> feignList(@RequestBody Product product) {
        Integer id = product.getPid();
        Product byId = productMapper.getById(id);
        SubResponse<Product> ok = new SubResponse<>("500", "error");
        return SubResponse.errorResult("600,", "xxx");
    }
}
