package com.jjc.service.netty.rpc.provider;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @description:
 * @author: jjc
 * @createTime: 2021/5/6
 */
public class ProviderHandlerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                .addFirst(new ObjectEncoder())
                // maxObjectSize: 将单个对象序列化之后的字节数组长度设置为1M
                // classResolver: 使用weakCachingConcurrentResolver创建线程安全的WeakReferenceMap对类加载器进行缓存,它支持多线程并发访问,当虚拟机内存不足时,会释放缓存中的内存,防止内存泄露,为了防止异常码流和解码错位导致的内存溢出
                .addLast(new ObjectDecoder(1024*1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())))
                .addLast(new ProviderHandler());
    }
}