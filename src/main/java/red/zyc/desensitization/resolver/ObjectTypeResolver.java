/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package red.zyc.desensitization.resolver;

import red.zyc.desensitization.annotation.SensitiveAnnotation;
import red.zyc.desensitization.desensitizer.Desensitizer;
import red.zyc.desensitization.exception.DesensitizationException;
import red.zyc.desensitization.support.InstanceCreators;
import red.zyc.desensitization.util.ReflectionUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 解析被<b>直接</b>标注敏感注解的对象，只会处理对象上直接存在的第一个敏感注解。
 *
 * @author zyc
 * @see SensitiveAnnotation
 */
public class ObjectTypeResolver implements TypeResolver<Object, AnnotatedType> {

    /**
     * 脱敏器缓存
     */
    private static final ConcurrentMap<Class<Desensitizer<Object, Annotation>>, Desensitizer<Object, Annotation>> DESENSITIZER_CACHE = new ConcurrentHashMap<>();

    /**
     * 敏感注解中脱敏器方法缓存
     */
    private static final ConcurrentMap<Class<? extends Annotation>, Method> DESENSITIZER_METHOD_CACHE = new ConcurrentHashMap<>();

    @Override
    public Object resolve(Object value, AnnotatedType annotatedType) {
        return Arrays.stream(annotatedType.getDeclaredAnnotations())
                .filter(annotation -> annotation.annotationType().isAnnotationPresent(SensitiveAnnotation.class))
                .findFirst()
                .map(sensitiveAnnotation -> getDesensitizer(sensitiveAnnotation).desensitize(value, sensitiveAnnotation))
                .orElse(value);
    }

    @Override
    public boolean support(Object value, AnnotatedType annotatedType) {
        return value != null;
    }

    @Override
    public int order() {
        return LOWEST_PRIORITY - 1;
    }

    /**
     * 实例化敏感注解对应的{@link Desensitizer}。
     * 目前对同一个{@link Class}的脱敏器对象都添加了缓存。
     *
     * @param annotation 敏感注解
     * @return 敏感注解对应的 {@link Desensitizer}
     */
    private Desensitizer<Object, Annotation> getDesensitizer(Annotation annotation) {
        try {
            Method desensitizerMethod = DESENSITIZER_METHOD_CACHE.computeIfAbsent(annotation.annotationType(), annotationClass -> ReflectionUtil.getDeclaredMethod(annotationClass, "desensitizer"));
            return DESENSITIZER_CACHE.computeIfAbsent(ReflectionUtil.invokeMethod(annotation, desensitizerMethod), clazz -> InstanceCreators.getInstanceCreator(clazz).create());
        } catch (Exception e) {
            throw new DesensitizationException(String.format("实例化敏感注解%s的脱敏器失败。", annotation.annotationType()), e);
        }
    }

}
