package red.zyc.desensitization.handler;

import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 敏感信息处理者基类，为子类提供了一些快捷有用的方法处理敏感信息。
 *
 * @param <A> 实现类要处理的注解类型
 * @param <T> 实现类支持的处理类型
 * @author zyc
 */
@Getter
public abstract class AbstractSensitiveHandler<A extends Annotation, T> implements SensitiveHandler<A, T> {

    protected Class<A> annotationClass;

    protected Class<T> supportedClass;

    /**
     * 判断处理者是否支持将要处理的目标类型
     *
     * @param targetClass 需要擦除敏感信息的目标对象的 {@code Class}
     * @return 处理者是否支持目标类型
     */
    public boolean support(Class<?> targetClass) {
        return supportedClass.isAssignableFrom(targetClass);
    }

    @SuppressWarnings("unchecked")
    AbstractSensitiveHandler() {
        Type[] types = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        annotationClass = (Class<A>) types[0];
        supportedClass = (Class<T>) types[1];
    }

    /**
     * 这个方法的作用仅仅是用来类型转换
     *
     * @param target     {@link T}
     * @param annotation {@link A}
     * @return {@link T}
     */
    @SuppressWarnings("unchecked")
    public T handling(Object target, Annotation annotation) {
        return handle((T) target, (A) annotation);
    }

}
