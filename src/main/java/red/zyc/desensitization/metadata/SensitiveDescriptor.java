package red.zyc.desensitization.metadata;

import red.zyc.desensitization.annotation.Sensitive;
import red.zyc.desensitization.util.CallerUtil;
import sun.invoke.util.BytecodeDescriptor;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
     * <ol>
     *     <li>
     *         如果{@code this}是Lambda表达式，则返回Lambda表达式的类型参数上的第一个敏感注解
     *     </li>
     *     <li>
     *         如果{@code this}是匿名内部类，则返回注释在改类上的第一个敏感注解
     *     </li>
     * </ol>
     *
     * @return 敏感描述者承载的第一个敏感注解
     */
    default A getSensitiveAnnotation() {
        try {
            Class<?> clazz = getClass();
            // Lambda
            if (clazz.isSynthetic()) {
                Method writeReplace = clazz.getDeclaredMethod("writeReplace");
                writeReplace.setAccessible(true);
                SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(this);
                List<Class<?>> classes = BytecodeDescriptor.parseMethod(serializedLambda.getImplMethodSignature(), Thread.currentThread().getContextClassLoader());
                Method lambdaStaticMethod = CallerUtil.getCaller(2).getDeclaredMethod(serializedLambda.getImplMethodName(), classes.get(0));
                lambdaStaticMethod.setAccessible(true);
                Annotation[] annotations = lambdaStaticMethod.getParameters()[0].getAnnotations();
                if (annotations.length > 0) {
                    // 只返回生成的Lambda方法参数上的第一个敏感注解
                    for (Annotation annotation : annotations) {
                        if (annotation.annotationType().isAnnotationPresent(Sensitive.class)) {
                            @SuppressWarnings("unchecked")
                            A sensitiveAnnotation = (A) annotation;
                            return sensitiveAnnotation;
                        }
                    }
                }
                // AnonymousClass
            } else {
                Annotation[] annotations = clazz.getAnnotations();
                if (annotations.length > 0) {
                    // 只返回匿名内部类上的第一个敏感注解
                    for (Annotation annotation : annotations) {
                        if (annotation.annotationType().isAnnotationPresent(Sensitive.class)) {
                            @SuppressWarnings("unchecked")
                            A sensitiveAnnotation = (A) annotation;
                            return sensitiveAnnotation;
                        }
                    }
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
