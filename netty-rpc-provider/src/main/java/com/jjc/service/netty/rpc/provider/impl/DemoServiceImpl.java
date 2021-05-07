package com.jjc.service.netty.rpc.provider.impl;

import com.jjc.service.netty.rpc.contract.DemoService;

/**
 * @description:
 * @author: jjc
 * @createTime: 2021/5/6
 */
public class DemoServiceImpl implements DemoService {

    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }
}