package com.atom.mallweb;

import java.util.List;

/**
 * 轮询
 *
 * @author Atom
 */
public class LoadBalance {

    private List<String> services;
    private Integer index = 0;

    public LoadBalance(List<String> services) {
        this.services = services;
    }

    public String choose() {
        String service = services.get(index);
        index++;
        if (index > services.size()) {
            index = 0;
        }
        System.out.println("choosed service:" + service);
        return service;
    }
}
