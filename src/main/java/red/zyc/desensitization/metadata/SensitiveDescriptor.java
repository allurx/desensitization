package red.zyc.desensitization.metadata;

import red.zyc.desensitization.annotation.Sensitive;
import red.zyc.desensitization.util.CallerUtil;
import sun.invoke.util.BytecodeDescriptor;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * 单个敏感信息描述者，用来承载敏感信息注解，实现{@link Serializable}以在运行时获取{@link SerializedLambda}
 *
 * @param <T>
 * @author zyc
 * @see SerializedLambda
 */
@FunctionalInterface
public interface SensitiveDescriptor<T, A extends Annotation> extends Serializable {

    /**
     * 判断单个敏感值对象是否是容器类型的敏感值，例如：
     * <ul>
     *     <li>
     *         包含{@link String}类型敏感值的{@link Collection}
     *     </li>
     *     <li>
     *         包含{@link String}类型敏感值的{@link Array}
     *     </li>
     * </ul>
     * 目前只处理以上几种类型
     *
     * @param value 敏感字段值
     * @return 是否是容器类型的敏感值
     */
    boolean isContainer(T value);

    /**
     * <ol>
     *     <li>
     *         如果{@code this}是Lambda表达式，则返回Lambda表达式的类型参数上的第一个敏感注解
     *     </li>
     *     <li>
     *         如果{@code this}是匿名内部类，则返回注释在该类上的第一个敏感注解
     *     </li>
     * </ol>
     *
     * @return 敏感描述者承载的第一个敏感注解，如果没有在敏感描述者上标记敏感注解则返回{@code null}
     * @throws Throwable 任何可能的异常
     */
    default A getSensitiveAnnotation() throws Throwable {
        Class<?> clazz = getClass();
        // Lambda
        if (clazz.isSynthetic()) {
            Method writeReplace = clazz.getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(this);
            List<Class<?>> classes = BytecodeDescriptor.parseMethod(serializedLambda.getImplMethodSignature(), Thread.currentThread().getContextClassLoader());
            Method lambdaStaticMethod = CallerUtil.getCaller(4).getDeclaredMethod(serializedLambda.getImplMethodName(), classes.get(0));
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
        return null;
    }
}
