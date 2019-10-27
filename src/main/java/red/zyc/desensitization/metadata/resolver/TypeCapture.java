package red.zyc.desensitization.metadata.resolver;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 捕获{@link T}的明确类型
 *
 * @param <T>
 * @author zyc
 */
abstract class TypeCapture<T> {

    /**
     * {@link T}运行时的{@link Type}
     */
    protected final Type type;

    /**
     * {@link T}运行时的{@link AnnotatedType}
     */
    protected final AnnotatedType annotatedType;

    TypeCapture() {
        Type superclass = getClass().getGenericSuperclass();
        if (!(superclass instanceof ParameterizedType)) {
            throw new IllegalArgumentException(getClass() + "必须是参数化类型");
        }
        type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        annotatedType = ((AnnotatedParameterizedType) getClass().getAnnotatedSuperclass()).getAnnotatedActualTypeArguments()[0];
    }
}
