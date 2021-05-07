package com.jjc.service.netty.rpc.consumer;

import java.lang.reflect.Proxy;

/**
 * @description: Dubbo consumer端代理类，获取clazz(要调用的provider端接口)的代理对象实例
 * @author: jjc
 * @createTime: 2021/5/6
 */
public class DubboProxy {

    public static Object getProxyInstance(Class<?> clazz) {
        // 动态代理
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new ConsumerInvocationHandler());
    }
}
