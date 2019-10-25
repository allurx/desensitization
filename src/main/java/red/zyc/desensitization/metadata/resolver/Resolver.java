package red.zyc.desensitization.metadata.resolver;

import red.zyc.desensitization.SensitiveUtil;
import red.zyc.desensitization.util.Optional;
import red.zyc.desensitization.util.ReflectionUtil;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * 类型解析器，用来解析一些特殊的数据类型。例如{@link Collection}，{@link Map}，{@link Array}等类型。
 * 用户可以实现该接口定义特定类型的解析器，然后调用{@link Resolvers#register}方法来注册自己的解析器。
 *
 * @param <T> 类型解析器支持的处理类型
 * @author zyc
 * @see CollectionResolver
 * @see MapResolver
 * @see ArrayResolver
 */
public interface Resolver<T> {


    /**
     * 解析对象
     *
     * @param value         将要解析的对象
     * @param annotatedType 将要解析的对象的{@link AnnotatedType}
     * @return 解析的新对象
     */
    T resolve(T value, AnnotatedType annotatedType);

    /**
     * 根据相应注册的{@link Resolver}解析对象
     *
     * @param value         对象值
     * @param annotatedType 将要解析的对象的{@link AnnotatedType}
     * @return 解析的新对象
     */
    default Object resolving(Object value, AnnotatedType annotatedType) {
        // 能够根据类型解析器解析的值
        Object resolved = Optional.ofNullable(Resolvers.getResolver(annotatedType))
                .map(resolver -> resolver.resolve(value, annotatedType))
                .orElse(value);
        // 根据对象上的敏感注解擦除敏感信息
        return Optional.ofNullable(ReflectionUtil.getFirstSensitiveAnnotationOnAnnotatedType(annotatedType))
                .map(sensitiveAnnotation -> SensitiveUtil.handling(resolved, sensitiveAnnotation))
                .or(() -> Optional.ofNullable(ReflectionUtil.getEraseSensitiveAnnotationOnAnnotatedType(annotatedType))
                        .map(eraseSensitiveAnnotation -> SensitiveUtil.desensitize(resolved)))
                .orElse(resolved);
    }

}
