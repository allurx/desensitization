package red.zyc.desensitization.metadata.resolver;

import java.lang.reflect.AnnotatedParameterizedType;
import java.util.Arrays;

/**
 * @author zyc
 */
public abstract class TypeCapture<T> {

    public TypeCapture(){
        System.out.println(this.getClass().getGenericSuperclass());
        AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) this.getClass().getAnnotatedSuperclass();
        AnnotatedParameterizedType annotatedActualTypeArgument = (AnnotatedParameterizedType) annotatedParameterizedType.getAnnotatedActualTypeArguments()[0];
        System.out.println(Arrays.toString(annotatedActualTypeArgument.getAnnotatedActualTypeArguments()[0].getAnnotations()));
    }
}
