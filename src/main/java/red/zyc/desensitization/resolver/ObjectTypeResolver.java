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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 解析被直接标注敏感注解的对象，只会处理对象上直接存在的第一个敏感注解。
 *
 * @author zyc
 */
public class ObjectTypeResolver implements TypeResolver<Object, AnnotatedType> {

    /**
     * 脱敏器缓存
     */
    private static final ConcurrentMap<Class<? extends Desensitizer<?, ? extends Annotation>>, Desensitizer<?, ? extends Annotation>> DESENSITIZER_CACHE = new ConcurrentHashMap<>();

    /**
     * 脱敏器方法缓存
     */
    private static final ConcurrentMap<Class<? extends Annotation>, Method> DESENSITIZER_METHOD_CACHE = new ConcurrentHashMap<>();

    @Override
    public Object resolve(Object value, AnnotatedType annotatedType) {
        Annotation sensitiveAnnotation = getFirstDirectlyPresentSensitiveAnnotation(annotatedType);
        return Optional.ofNullable(sensitiveAnnotation)
                .map(this::getDesensitizer)
                .map(desensitizer -> desensitizer.desensitize(value, sensitiveAnnotation))
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
     * 目前对同一个{@link Class}的脱敏器都添加了缓存，也就是说每个脱敏器都是
     * 以单例的形式存在的，如何后期需要创建同一类型脱敏器的多个实例，就需要添加一个配置来控制。
     *
     * @param annotation 敏感注解
     * @param <T>        脱敏器支持的目标类型
     * @param <A>        脱敏器支持的注解类型
     * @return 敏感注解对应的 {@link Desensitizer}
     */
    @SuppressWarnings("unchecked")
    private <T, A extends Annotation> Desensitizer<T, A> getDesensitizer(A annotation) {
        try {
            Method desensitizerMethod = DESENSITIZER_METHOD_CACHE.computeIfAbsent(annotation.annotationType(), annotationClass -> ReflectionUtil.getDeclaredMethod(annotationClass, "desensitizer"));
            Class<Desensitizer<T, A>> desensitizerClass = (Class<Desensitizer<T, A>>) desensitizerMethod.invoke(annotation);
            return (Desensitizer<T, A>) DESENSITIZER_CACHE.computeIfAbsent(desensitizerClass, clazz -> InstanceCreators.getInstanceCreator(clazz).create());
        } catch (Exception e) {
            throw new DesensitizationException(String.format("实例化敏感注解%s的脱敏器失败。", annotation.annotationType()), e);
        }
    }

    /**
     * 获取{@link AnnotatedType}上的第一个直接存在的敏感注解
     *
     * @param annotatedType {@link AnnotatedType}对象
     * @return {@link AnnotatedType}上的第一个敏感注解
     */
    private Annotation getFirstDirectlyPresentSensitiveAnnotation(AnnotatedType annotatedType) {
        Annotation[] annotations = annotatedType.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(SensitiveAnnotation.class)) {
                return annotation;
            }
        }
        return null;
    }

}
