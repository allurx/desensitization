package red.zyc.desensitization.metadata;

import red.zyc.desensitization.util.CallerUtil;
import sun.invoke.util.BytecodeDescriptor;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 敏感信息描述者，用来承载敏感信息注解
 *
 * @param <T>
 */
@FunctionalInterface
public interface SensitiveDescriptor<T, A extends Annotation> extends Serializable {

    /**
     * @param value 敏感字段值
     */
    void describe(T value);

    /**
     * 获取当前敏感处理器上的第一个敏感处理注解
     *
     * @return 目标对象上的第一个敏感处理注解
     */
    default A getSensitiveAnnotation() {
        try {
            Method writeReplace = this.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(this);
            List<Class<?>> classes = BytecodeDescriptor.parseMethod(serializedLambda.getImplMethodSignature(), Thread.currentThread().getContextClassLoader());
            Method lambdaStaticMethod = CallerUtil.getCaller(2).getDeclaredMethod(serializedLambda.getImplMethodName(), classes.get(0));
            lambdaStaticMethod.setAccessible(true);
            @SuppressWarnings("unchecked")
            A sensitiveAnnotation = (A) lambdaStaticMethod.getParameters()[0].getAnnotations()[0];
            return sensitiveAnnotation;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
