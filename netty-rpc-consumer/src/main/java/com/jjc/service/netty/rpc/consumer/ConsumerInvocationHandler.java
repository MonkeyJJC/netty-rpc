package com.jjc.service.netty.rpc.consumer;

import com.jjc.service.netty.rpc.common.dto.DubboRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @description: consumer端拦截器，实现InvocationHandler，作用是当consumer端调用代理对象的目标方法时，实际是在连接Netty服务端，并将要调用的方法信息放在协议DubboRequest中传给provider端
 * InvocationHandler作用就是，当代理对象的原本方法被调用的时候，会重定向到一个方法，这个方法就是InvocationHandler里面定义的内容，同时会替代原本方法的结果返回。
 * InvocationHandler接收三个参数：proxy，代理后的实例对象。 method，对象被调用方法。args，调用时的参数。
 * @author: jjc
 * @createTime: 2021/5/7
 */
@Slf4j
public class ConsumerInvocationHandler implements InvocationHandler {

    private Object res;

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        EventLoopGroup eventGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventGroup)
                    .channel(NioSocketChannel.class)
                    // 本地provider，直接localhost
                    .remoteAddress("127.0.0.1", 8888)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    .addFirst(new ObjectEncoder())
                                    .addLast(new ObjectDecoder(1024*1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())))
                                    .addLast(new ConsumerHandler(proxy, method, args));
                        }
                    });
            ChannelFuture f = bootstrap.connect().sync();
            f.channel().closeFuture().sync();
        } finally {
            eventGroup.shutdownGracefully();
        }
        return res;
    }

    /**
     * consumer端拦截器
     */
    private class ConsumerHandler extends ChannelInboundHandlerAdapter {

        private Object proxy;
        private Method method;
        private Object[] args;

        public ConsumerHandler(Object proxy, Method method, Object[] args) {
            this.proxy = proxy;
            this.method = method;
            this.args = args;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            // 传输的对象必须实现序列化接口
            DubboRequest request = new DubboRequest(proxy.getClass().getInterfaces()[0], method.getName(), method.getParameterTypes(), args);
            ctx.writeAndFlush(request);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("consumer调用provider成功");
            res = msg;
            ctx.flush();
            // 收到响应后断开连接
            ctx.close();
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }
    }
}