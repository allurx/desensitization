package red.zyc.desensitization.metadata.resolver;

import java.lang.reflect.AnnotatedType;
import java.util.Collection;

/**
 * @author zyc
 */
public interface Resolver<T> {

    CollectionResolver COLLECTION_RESOLVER = new CollectionResolver();

    MapResolver MAP_RESOLVER = new MapResolver();

    ArrayResolver ARRAY_RESOLVER = new ArrayResolver();

    /**
     * 解析对象
     *
     * @param value         将要解析的对象
     * @param typeArguments 将要解析的对象的{@link AnnotatedType}
     */
    T resolve(T value, AnnotatedType... typeArguments);

    T resolveOther(T value, AnnotatedType typeArgument);

    Collection<?> resolveOther(Collection<?> value, AnnotatedType typeArgument);
}
