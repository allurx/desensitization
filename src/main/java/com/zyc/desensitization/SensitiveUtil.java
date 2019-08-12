package com.zyc.desensitization;

import com.zyc.desensitization.annotation.EraseSensitive;
import com.zyc.desensitization.annotation.Sensitive;
import com.zyc.desensitization.handler.AbstractSensitiveHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author zyc
 */
@Slf4j
public class SensitiveUtil {

    /**
     * 保存被处理过的对象
     */
    private static ThreadLocal<List<Object>> targets = ThreadLocal.withInitial(ArrayList::new);

    /**
     * 处理复杂的对象
     *
     * @param target 目标对象
     */
    public static void handle(Object target) {
        if (target == null) {
            return;
        }
        Class<?> targetClass = target.getClass();
        // 目标对象是集合
        if (Collection.class.isAssignableFrom(targetClass)) {
            Collection<?> collection = (Collection<?>) target;
            collection.forEach(SensitiveUtil::handle);
        }
        // 目标对象是数组
        if (targetClass.isArray()) {
            Object[] objects = (Object[]) target;
            Arrays.stream(objects).forEach(SensitiveUtil::handle);
        }
        // 目标是普通实体
        handleBean(target);
    }


    /**
     * 处理普通的对象
     *
     * @param target 目标对象
     */
    private static void handleBean(Object target) {
        try {
            // 引用嵌套
            if (isReferenceNested(target)) {
                return;
            }
            Class<?> targetClass = target.getClass();
            Field[] allFields = getAllFields(targetClass);
            for (Field field : allFields) {
                field.setAccessible(true);
                Object fieldValue = field.get(target);
                if (fieldValue == null) {
                    continue;
                }
                // 级联擦除域中的敏感信息
                if (field.isAnnotationPresent(EraseSensitive.class)) {
                    handle(fieldValue);
                }
                // 找出field上的敏感注解
                Annotation[] annotations = field.getAnnotations();
                for (Annotation annotation : annotations) {
                    Class<? extends Annotation> annotationClass = annotation.annotationType();
                    if (annotationClass.isAnnotationPresent(Sensitive.class)) {
                        Method method = annotationClass.getDeclaredMethod("handler");
                        @SuppressWarnings("unchecked")
                        Class<? extends AbstractSensitiveHandler<? extends Annotation, ?>> c = (Class<? extends AbstractSensitiveHandler<? extends Annotation, ?>>) method.invoke(annotation);
                        AbstractSensitiveHandler<? extends Annotation, ?> sensitiveHandler = c.newInstance();
                        Class<?> fieldClass = field.getType();
                        if (sensitiveHandler.support(fieldClass)) {
                            field.set(target, sensitiveHandler.handling(fieldValue, annotation));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 目标对象可能被循环引用嵌套
     *
     * @param target 目标对象
     * @return 目标对象之前是否已经被处理过
     */
    private static boolean isReferenceNested(Object target) {
        List<Object> list = targets.get();
        for (Object o : list) {
            // 没有使用contains方法，仅仅比较目标是否引用同一个对象
            if (o == target) {
                return true;
            }
        }
        list.add(target);
        return false;
    }

    /**
     * @param targetClass 目标对象的{@code Class}
     * @return 目标对象以及所有父类定义的 {@link Field}
     */
    private static Field[] getAllFields(Class<?> targetClass) {
        List<Field> fields = new ArrayList<>(Arrays.asList(targetClass.getDeclaredFields()));
        Class<?> superclass = targetClass.getSuperclass();
        while (superclass != null && superclass != Object.class) {
            fields.addAll(Arrays.asList(superclass.getDeclaredFields()));
            superclass = superclass.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

}
