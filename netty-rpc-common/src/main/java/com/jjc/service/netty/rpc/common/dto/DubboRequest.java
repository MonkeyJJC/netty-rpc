package com.jjc.service.netty.rpc.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @description: 模拟dubbo请求类
 * @author: jjc
 * @createTime: 2021/5/6
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DubboRequest implements Serializable {
    private static final long serialVersionUID = 8473698001818105266L;
    private Class<?> interfaceClass;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] args;

    @Override
    public String toString() {
        return "DubboRequest{" +
                "interfaceClass=" + interfaceClass +
                ", methodName='" + methodName + '\'' +
                ", paramTypes=" + Arrays.toString(paramTypes) +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}