package com.zyc.desensitization.handler;

import java.lang.annotation.Annotation;


/**
 * @param <A> 实现类要处理的注解类型
 * @param <T> 实现类支持的处理类型
 * @author zyc
 */
public interface SensitiveHandler<A extends Annotation, T> {

    /**
     * 处理敏感信息
     *
     * @param target     需要处理的目标
     * @param annotation 处理目标上的敏感注解
     * @return 处理后的结果
     */
    T handle(T target, A annotation);
}
