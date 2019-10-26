/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package red.zyc.desensitization;

import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.LocalVariable;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.TypeReference;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.zyc.desensitization.annotation.ChineseNameSensitive;
import red.zyc.desensitization.annotation.EmailSensitive;
import red.zyc.desensitization.metadata.SensitiveDescriptor;
import red.zyc.desensitization.metadata.resolver.TypeCapture;
import red.zyc.desensitization.model.Child;
import red.zyc.desensitization.util.CallerUtil;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * @author zyc
 */
public class Example implements Serializable {

    private Logger log = LoggerFactory.getLogger(Example.class);

    /**
     * 对象内部域值脱敏
     */
    @Test
    public void desensitizeObject() {
        Child child = new Child();
        log.info("before擦除复杂对象内部敏感信息:{}", child.toString());
        Child c = SensitiveUtil.desensitize(child);
        log.info("after擦除复杂对象内部敏感信息:{}", c);
    }

    /**
     * 单个值脱敏
     */
    @Test
    public void desensitizeValue() {

        String email = "123456@qq.com";

        log.info("before擦除单个敏感者:{}", email);

        // 使用Lambda表达式
        String email1 = SensitiveUtil.desensitize(email, (@EmailSensitive String value) -> {
        });
        log.info("after使用Lambda表达式指定单个敏感信息描述者:{}", email1);


        // 使用匿名内部类
        String email2 = SensitiveUtil.
                desensitize(email, new SensitiveDescriptor<String>() {
                    @Override
                    public void describe(@EmailSensitive String value) {

                    }
                });
        log.info("after使用匿名类指定单个敏感信息描述者:{}", email2);
    }

    /**
     * 集合内部值脱敏
     */
    @Test
    public void desensitizeCollection() {

        List<String> collection = new ArrayList<>(Arrays.asList("123456@qq.com", "1234567@qq.com", "12345678@qq.com"));

        log.info("before擦除Collection类型敏感值:{}", collection);

        // 使用Lambda表达式
        Collection<String> emails1 = SensitiveUtil.
                desensitize(collection,
                        (List<@EmailSensitive String> value) -> {
                        });
        log.info("after使用Lambda表达式指定Collection类型敏感信息描述者:{}", emails1);

        // 使用匿名内部类
        Collection<String> emails2 = SensitiveUtil.
                desensitize(new ArrayList<>(Arrays.asList("123456@qq.com", "1234567@qq.com", "12345678@qq.com")),
                        new SensitiveDescriptor<List<@EmailSensitive(placeholder = '%') String>>() {
                            @Override
                            public void describe(List<@EmailSensitive String> value) {

                            }
                        });
        log.info("after使用匿名类指定Collection类型敏感信息描述者:{}", emails2);
    }

    /**
     * 数组内部值脱敏
     */
    @Test
    public void desensitizeArray() {

        String[] emails = {"123456@qq.com", "1234567@qq.com", "12345678@qq.com"};

        log.info("before擦除Array类型敏感值:{}", Arrays.toString(emails));

        // 使用Lambda表达式
        String[] emails1 = SensitiveUtil.desensitize(emails,
                (@EmailSensitive String[] value) -> {
                });
        log.info("after使用Lambda表达式指定Array类型敏感信息描述者:{}", Arrays.toString(emails1));

        // 使用匿名内部类
        String[] emails2 = SensitiveUtil.
                desensitize(emails,
                        new SensitiveDescriptor<String[]>() {
                            @Override
                            public void describe(@EmailSensitive String[] element) {

                            }
                        });
        log.info("after使用匿名类指定Array类型敏感信息描述者:{}", Arrays.toString(emails2));
    }

    /**
     * Map内部值脱敏
     */
    @Test
    public void desensitizeMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("张三", "123456@qq.com");
        map.put("小明", "1234567@qq.com");
        map.put("李四", "1234568@qq.com");

        log.info("before擦除Map类型敏感值:{}", map);

        // 使用Lambda表达式
        Map<String, String> emails1 = SensitiveUtil.desensitize(map,
                (HashMap<@ChineseNameSensitive String, @EmailSensitive String> value) -> {
                });
        log.info("after使用Lambda表达式指定Map类型敏感信息描述者:{}", emails1);


        // 使用匿名内部类
        Map<String, String> emails2 = SensitiveUtil.
                desensitize(map,
                        new SensitiveDescriptor<HashMap<String, String>>() {
                            @Override
                            public void describe(HashMap<@ChineseNameSensitive String, @EmailSensitive String> value) {

                            }
                        });
        log.info("after使用匿名类指定Map类型敏感信息描述者:{}", emails2);
    }

    @Test
    public void printStackTrace() {
        log.info(CallerUtil.getCaller().toString());
        CallerUtil.printStackTrace();
    }

    @Test
    public void t() {
        TypeCapture<List<@EmailSensitive String>> t = new TypeCapture<List<@EmailSensitive String>>() {
        };
//        AnnotatedType annotatedSuperclass = t.getClass().getAnnotatedSuperclass();
//        AnnotatedParameterizedType annotatedParameterizedType= (AnnotatedParameterizedType) annotatedSuperclass;
//        AnnotatedParameterizedType annotatedActualTypeArgument = (AnnotatedParameterizedType) annotatedParameterizedType.getAnnotatedActualTypeArguments()[0];
//        System.out.println(Arrays.toString(annotatedActualTypeArgument.getAnnotatedActualTypeArguments()[0].getAnnotations()));
    }


    @Test
    public void tt() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(this);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        Constructor<LocalVariable> declaredConstructor = LocalVariable.class.getDeclaredConstructor(DataInputStream.class, ConstantPool.class);
        LocalVariable localVariable = declaredConstructor.newInstance(new DataInputStream(new ByteArrayInputStream(bytes)), null);
        System.out.println(localVariable);
    }

    @Test
    public void ttt() throws Exception {
        ClassReader reader = new ClassReader("red.zyc.desensitization.Example");
        final ClassNode cn = new ClassNode();
        reader.accept(cn, 0);
        for (MethodNode methodNode : cn.methods) {
            if (methodNode.name.equals("t")) {
                TypeReference typeReference=new TypeReference(methodNode.visibleLocalVariableAnnotations.get(0).typeRef);
                System.out.println(1);
            }
        }
    }
}
