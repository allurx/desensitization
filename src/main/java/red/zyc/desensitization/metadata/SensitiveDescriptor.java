package red.zyc.desensitization.metadata;

import red.zyc.desensitization.annotation.Sensitive;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * 单个敏感信息描述者，用来承载敏感信息注解，实现{@link Serializable}以在运行时获取{@link SerializedLambda}
 *
 * @param <T>
 * @author zyc
 * @see SerializedLambda
 */
@FunctionalInterface
public interface SensitiveDescriptor<T> extends Descriptor {

    /**
     * 用来承载敏感注解以处理敏感信息
     *
     * @param value 敏感信息值
     */
    void describe(T value);

    /**
     * 获取{@link SensitiveDescriptor#describe(T)}参数上的第一个敏感注解
     *
     * @return {@link SensitiveDescriptor#describe(T)}参数上的第一个敏感注解
     */
    @Override
    default Annotation getSensitiveAnnotation() {
        Method description = getDescription();
        Annotation[] annotations = description.getParameters()[0].getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(Sensitive.class)) {
                return annotation;
            }
        }
        return null;
    }


}
