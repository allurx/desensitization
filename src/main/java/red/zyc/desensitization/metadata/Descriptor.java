package red.zyc.desensitization.metadata;

import sun.invoke.util.BytecodeDescriptor;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * @author zyc
 */
public interface Descriptor extends Serializable {

    /**
     * 获取描述方法
     * <ol>
     *     <li>
     *         如果{@code this}是Lambda表达式，则返回Lambda表达式生成的静态方法
     *     </li>
     *     <li>
     *         如果{@code this}是匿名类，则返回该类重写的方法
     *     </li>
     * </ol>
     *
     * @return 敏感描述者承载的第一个敏感注解，如果没有在敏感描述者上标记敏感注解则返回{@code null}
     */
    default Method getDescription() {
        try {
            Class<?> clazz = getClass();
            // Lambda
            if (clazz.isSynthetic()) {
                Method writeReplace = clazz.getDeclaredMethod("writeReplace");
                writeReplace.setAccessible(true);
                SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(this);
                List<Class<?>> classes = BytecodeDescriptor.parseMethod(serializedLambda.getImplMethodSignature(), Thread.currentThread().getContextClassLoader());
                Field capturingClassField = serializedLambda.getClass().getDeclaredField("capturingClass");
                capturingClassField.setAccessible(true);
                Class<?> capturingClass = (Class<?>) capturingClassField.get(serializedLambda);
                Method lambdaStaticMethod = capturingClass.getDeclaredMethod(serializedLambda.getImplMethodName(), Arrays.copyOfRange(classes.toArray(new Class<?>[0]), 0, classes.size() - 1));
                lambdaStaticMethod.setAccessible(true);
                return lambdaStaticMethod;
                // AnonymousClass
            } else if (clazz.isAnonymousClass()) {
                Type[] actualTypeArguments = ((ParameterizedType) clazz.getGenericInterfaces()[0]).getActualTypeArguments();
                return clazz.getDeclaredMethod("describe", Arrays.copyOf(actualTypeArguments, actualTypeArguments.length, Class[].class));
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        throw new RuntimeException();
    }

    /**
     * 获取敏感注解，继承该接口的其它接口必须重写该方法。
     *
     * @return 敏感注解
     */
    default Annotation getSensitiveAnnotation() {
        throw new RuntimeException();
    }
}
