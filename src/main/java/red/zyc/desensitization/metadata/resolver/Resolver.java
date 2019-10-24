package red.zyc.desensitization.metadata.resolver;

import java.lang.reflect.AnnotatedType;

/**
 * @author zyc
 */
public interface Resolver<T> {


    /**
     * 解析对象
     *
     * @param value         将要解析的对象
     * @param annotatedType 将要解析的对象的{@link AnnotatedType}
     */
    T resolve(T value, AnnotatedType annotatedType);

}
