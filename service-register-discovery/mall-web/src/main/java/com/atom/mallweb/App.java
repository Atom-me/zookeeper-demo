package com.atom.mallweb;

import com.atom.mallweb.web.Response;
import com.google.gson.Gson;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * @author Atom
 */
public class App {

    static String BASE_URL = "http://127.0.0.1:8080";

    public static void main(String[] args) {
        RestTemplate rest = new RestTemplate();
        rest.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        final String body = rest.getForObject(BASE_URL + "/soa/products", String.class);
        System.err.println(body);
        final Response resp = new Gson().fromJson(body, Response.class);
        System.out.println(resp.getCode());
        System.out.println(resp.getMsg());
        System.out.println(resp.getData());
    }
}
