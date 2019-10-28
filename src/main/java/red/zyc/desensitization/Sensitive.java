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
package red.zyc.desensitization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.zyc.desensitization.desensitizer.Desensitizer;
import red.zyc.desensitization.exception.DesensitizerNotFoundException;
import red.zyc.desensitization.metadata.resolver.Resolvers;
import red.zyc.desensitization.metadata.resolver.TypeToken;
import red.zyc.desensitization.util.Optional;
import red.zyc.desensitization.util.ReflectionUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zyc
 */
public final class Sensitive {


    /**
     * 保存被脱敏过的对象
     */
    private static final ThreadLocal<List<Object>> TARGETS = ThreadLocal.withInitial(ArrayList::new);

    /**
     * {@link Logger}
     */
    private static final Logger LOG = LoggerFactory.getLogger(Sensitive.class);

    /**
     * 对象内部域值脱敏，注意该方法会改变原对象内部的域值。
     *
     * @param <T>    目标对象类型
     * @param target 目标对象
     */
    public static <T> T desensitize(T target) {
        try {
            handle(target);
        } finally {
            TARGETS.remove();
        }
        return target;
    }

    /**
     * 单个值脱敏
     *
     * @param target    目标对象
     * @param typeToken {@link TypeToken}
     * @param <T>       目标对象类型
     * @return 敏感信息被擦除后的值
     */
    public static <T> T desensitize(T target, TypeToken<T> typeToken) {
        return Optional.ofNullable(target)
                .map(t -> typeToken)
                .map(TypeToken::getAnnotatedType)
                .map(annotatedType -> Resolvers.resolving(target, annotatedType))
                .orElse(target);
    }

    /**
     * 脱敏复杂的对象
     *
     * @param target 目标对象
     */
    private static void handle(Object target) {
        try {
            if (target == null) {
                return;
            }
            // 引用嵌套
            if (isReferenceNested(target)) {
                return;
            }
            Class<?> targetClass = target.getClass();
            Field[] allFields = ReflectionUtil.listAllFields(targetClass);
            for (Field field : allFields) {
                // 跳过final修饰的field
                if (Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                // 跳过值为null的field
                Object fieldValue = field.get(target);
                if (fieldValue == null) {
                    continue;
                }
                field.set(target, Resolvers.resolving(fieldValue, field.getAnnotatedType()));
            }
        } catch (Throwable e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * 通过反射实例化敏感注解对应的{@link Desensitizer}
     *
     * @param annotation 敏感注解
     * @param <T>        目标对象的类型
     * @param <A>        敏感注解类型
     * @return 敏感注解对应的 {@link Desensitizer}
     */
    private static <T, A extends Annotation> Desensitizer<T, A> getDesensitizer(A annotation) {
        try {
            Class<? extends Annotation> annotationClass = annotation.annotationType();
            Method method = annotationClass.getDeclaredMethod("desensitizer");
            @SuppressWarnings("unchecked")
            Class<? extends Desensitizer<T, A>> handlerClass = (Class<? extends red.zyc.desensitization.desensitizer.Desensitizer<T, A>>) method.invoke(annotation);
            return handlerClass.newInstance();
        } catch (Throwable t) {
            LOG.error(t.getMessage(), t);
        }
        throw new DesensitizerNotFoundException("没有在" + annotation + "中找到脱敏器");
    }

    /**
     * 通过敏感注解处理相应的敏感值
     *
     * @param value               敏感值
     * @param sensitiveAnnotation 敏感注解
     * @param <T>                 敏感值类型
     * @return 脱敏后的值
     */
    public static <T> T handling(T value, Annotation sensitiveAnnotation) {
        return Sensitive.<T, Annotation>getDesensitizer(sensitiveAnnotation).desensitizing(value, sensitiveAnnotation);
    }

    /**
     * 目标对象可能被引用嵌套
     *
     * @param target 目标对象
     * @return 目标对象之前是否已经被脱敏过
     */
    private static boolean isReferenceNested(Object target) {
        List<Object> list = TARGETS.get();
        for (Object o : list) {
            // 没有使用contains方法，仅仅比较目标是否引用同一个对象
            if (o == target) {
                return true;
            }
        }
        list.add(target);
        return false;
    }
}
