package com.jjc.service.netty.rpc.provider;

import com.jjc.service.netty.rpc.common.dto.DubboRequest;
import com.jjc.service.netty.rpc.contract.DemoService;
import com.jjc.service.netty.rpc.provider.impl.DemoServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @description: provider端拦截器，进行invoke
 * @author: jjc
 * @createTime: 2021/5/6
 */
@Slf4j
public class ProviderHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("provider端收到消息：{}", msg);
        if (!(msg instanceof DubboRequest)) {
            ctx.fireChannelRead(msg);
        }
        // handle request
        DubboRequest request = (DubboRequest) msg;
        // 通过反射调用方法
        Object target = getInstanceByInterfaceClass(request.getInterfaceClass());
        String methodName = request.getMethodName();
        Method method = target.getClass().getMethod(methodName, request.getParamTypes());
        Object response = method.invoke(target, request.getArgs());
        ctx.writeAndFlush(response);
    }

    /**
     * 根据接口Class获取对应对象实例
     * @return
     */
    private Object getInstanceByInterfaceClass(Class<?> clazz) {
        if (DemoService.class.equals(clazz)) {
            return new DemoServiceImpl();
        }
        return null;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}