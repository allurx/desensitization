package red.zyc.desensitization.metadata;

import red.zyc.desensitization.annotation.Sensitive;

import java.lang.annotation.Annotation;

/**
 * @author zyc
 */
@FunctionalInterface
public interface MapSensitiveDescriptor<K, V> extends Descriptor {

    /**
     * 用来承载敏感注解以处理Map内的敏感信息
     *
     * @param key   {@link K}
     * @param value {@link V}
     */
    void describe(K key, V value);

    /**
     * 获取对应的key描述者
     *
     * @return key描述者
     */
    default KeySensitiveDescriptor<K> keySensitiveDescriptor() {
        return new KeySensitiveDescriptor<K>() {

            @Override
            public void describe(K value) {

            }

            @Override
            public Annotation getSensitiveAnnotation() {
                Annotation[] annotations = MapSensitiveDescriptor.this.getDescription().getParameters()[0].getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().isAnnotationPresent(Sensitive.class)) {
                        return annotation;
                    }
                }
                return null;
            }
        };
    }

    /**
     * 获取对应的value描述者
     *
     * @return value描述者
     */
    default ValueSensitiveDescriptor<V> valueSensitiveDescriptor() {
        return new ValueSensitiveDescriptor<V>() {

            @Override
            public void describe(V value) {

            }

            @Override
            public Annotation getSensitiveAnnotation() {
                Annotation[] annotations = MapSensitiveDescriptor.this.getDescription().getParameters()[1].getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().isAnnotationPresent(Sensitive.class)) {
                        return annotation;
                    }
                }
                return null;
            }
        };
    }

    interface KeySensitiveDescriptor<E> extends SensitiveDescriptor<E> {

    }

    interface ValueSensitiveDescriptor<E> extends SensitiveDescriptor<E> {

    }

}
