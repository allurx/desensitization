package red.zyc.desensitization.metadata.resolver;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * @author zyc
 */
public interface Resolver<T> {

    CollectionResolver COLLECTION_RESOLVER = new CollectionResolver();

    MapResolver MAP_RESOLVER = new MapResolver();

    /**
     * 解析对象
     *
     * @param value         将要解析的对象
     * @param typeArguments 将要解析的对象的{@link AnnotatedType}
     */
    void resolve(T value, AnnotatedType... typeArguments);

    void resolveOther(T value, AnnotatedType typeArgument);

    default void resolves(T value, AnnotatedType typeArgument) {
        if (typeArgument instanceof AnnotatedParameterizedType) {
            AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) typeArgument;
            ParameterizedType type = (ParameterizedType) typeArgument.getType();
            Class<?> rawTypeOfTypeArgument = (Class<?>) type.getRawType();
            if (Collection.class.isAssignableFrom(rawTypeOfTypeArgument)) {
                COLLECTION_RESOLVER.resolve((Collection<?>) value, annotatedParameterizedType.getAnnotatedActualTypeArguments()[0]);
            } else if (Map.class.isAssignableFrom(rawTypeOfTypeArgument)) {
                MAP_RESOLVER.resolve(((Map<?, ?>) value).keySet(), annotatedParameterizedType.getAnnotatedActualTypeArguments()[0]);
                MAP_RESOLVER.resolve(((Map<?, ?>) value).values(), annotatedParameterizedType.getAnnotatedActualTypeArguments()[1]);
            } else {
                resolveOther((T) value, typeArgument);
            }
        } else if (typeArgument instanceof AnnotatedArrayType) {
            AnnotatedType annotatedGenericComponentType = ((AnnotatedArrayType) typeArgument).getAnnotatedGenericComponentType();

        } else if (typeArgument instanceof AnnotatedTypeVariable) {
            AnnotatedTypeVariable annotatedTypeVariable = (AnnotatedTypeVariable) typeArgument;
            AnnotatedType[] annotatedBounds = annotatedTypeVariable.getAnnotatedBounds();
            Arrays.stream(annotatedBounds).forEach(annotatedBound -> {
                if (Collection.class.isAssignableFrom((Class<?>) annotatedBound.getType())
                        && annotatedBound instanceof AnnotatedParameterizedType) {
                    COLLECTION_RESOLVER.resolve((Collection<?>) value, ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments()[0]);
                } else if (Map.class.isAssignableFrom((Class<?>) annotatedBound.getType())
                        && annotatedBound instanceof AnnotatedParameterizedType) {
                    MAP_RESOLVER.resolve(((Map<?, ?>) value).keySet(), ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments()[0]);
                    MAP_RESOLVER.resolve(((Map<?, ?>) value).values(), ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments()[1]);
                } else {
                    resolveOther(value, typeArgument);
                }
            });
        } else if (typeArgument instanceof AnnotatedWildcardType) {
            AnnotatedWildcardType annotatedWildcardType = (AnnotatedWildcardType) typeArgument;
            AnnotatedType[] annotatedUpperBounds = annotatedWildcardType.getAnnotatedUpperBounds();
            AnnotatedType[] annotatedBounds = annotatedUpperBounds.length == 0 ? annotatedWildcardType.getAnnotatedLowerBounds() : annotatedUpperBounds;
            Arrays.stream(annotatedBounds).forEach(annotatedBound -> {
                if (Collection.class.isAssignableFrom((Class<?>) annotatedBound.getType())
                        && annotatedBound instanceof AnnotatedParameterizedType) {
                    ((Collection<?>) value).forEach(o -> COLLECTION_RESOLVER.resolve((Collection<?>) o, ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments()[0]));
                } else if (Map.class.isAssignableFrom((Class<?>) annotatedBound.getType())
                        && annotatedBound instanceof AnnotatedParameterizedType) {
                    MAP_RESOLVER.resolve(((Map<?, ?>) value).keySet(), ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments()[0]);
                    MAP_RESOLVER.resolve(((Map<?, ?>) value).values(), ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments()[1]);
                } else {
                    resolveOther(value, typeArgument);
                }
            });
            // AnnotatedTypeBaseImpl
        } else {
            resolveOther(value, typeArgument);
        }
    }
}
