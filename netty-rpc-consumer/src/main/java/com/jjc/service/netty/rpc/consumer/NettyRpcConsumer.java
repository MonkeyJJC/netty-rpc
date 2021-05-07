package com.jjc.service.netty.rpc.consumer;

import com.jjc.service.netty.rpc.contract.DemoService;

/**
 * @author jjc
 */
public class NettyRpcConsumer {

    public static void main(String[] args) throws Exception {
        // 动态代理，直接通过JDK提供的一个Proxy.newProxyInstance()创建了一个DemoService接口对象
        DemoService demoService = (DemoService) DubboProxy.getProxyInstance(DemoService.class);
        // 执行远程方法，实际执行的InvocationHandler
        String greetings = demoService.sayHello("jjc");
        System.out.println(greetings + " from separated thread.");
    }
}
