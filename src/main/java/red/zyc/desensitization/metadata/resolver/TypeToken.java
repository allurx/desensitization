package red.zyc.desensitization.metadata.resolver;

import java.io.Serializable;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;

/**
 * @param <T>
 * @author zyc
 */
public abstract class TypeToken<T> extends TypeCapture<T> implements Serializable {

    public Type getType() {
        return type;
    }

    public final AnnotatedType getAnnotatedType() {
        return annotatedType;
    }
}
