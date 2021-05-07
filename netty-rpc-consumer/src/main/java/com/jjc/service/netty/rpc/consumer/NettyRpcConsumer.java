package com.jjc.service.netty.rpc.consumer;

import com.jjc.service.netty.rpc.contract.DemoService;

/**
 * @author jjc
 */
public class NettyRpcConsumer {

    public static void main(String[] args) throws Exception {
        // 模拟引入jar获取协议
        DemoService demoService = (DemoService) DubboProxy.getProxyInstance(DemoService.class);
        // 执行远程方法
        String greetings = demoService.sayHello("jjc");
        System.out.println(greetings + " from separated thread.");
    }
}
