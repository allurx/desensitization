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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.zyc.desensitization.annotation.ChineseNameSensitive;
import red.zyc.desensitization.annotation.EmailSensitive;
import red.zyc.desensitization.metadata.MapSensitiveDescriptor;
import red.zyc.desensitization.metadata.SensitiveDescriptor;
import red.zyc.desensitization.model.Child;
import red.zyc.desensitization.model.Father;
import red.zyc.desensitization.model.Mother;
import red.zyc.desensitization.util.CallerUtil;

import java.lang.reflect.*;
import java.util.*;

/**
 * @author zyc
 */
public class Example {

    private Logger log = LoggerFactory.getLogger(Example.class);

    /**
     * 对象内部域值脱敏
     */
    @Test
    public void desensitizeObject() {
        Child child = new Child();
        child.getParents().add(new Father());
        child.getParents().add(new Mother());
        log.info("before:{}", child.toString());
        Child c = SensitiveUtil.desensitize(child);
        log.info("after:{}", c);
    }

    /**
     * 单个值脱敏
     */
    @Test
    public void desensitizeValue() {

        // 使用Lambda表达式
        String email1 = SensitiveUtil.desensitize("123456@qq.com", (@EmailSensitive String value) -> {
        });
        log.info("after使用Lambda表达式指定敏感信息描述者:{}", email1);


        // 使用匿名内部类
        String email2 = SensitiveUtil.
                desensitize("123456@qq.com", new SensitiveDescriptor<String>() {
                    @Override
                    public void describe(@EmailSensitive String value) {

                    }
                });
        log.info("after使用匿名类指定敏感信息描述者:{}", email2);
    }

    /**
     * 集合内部值脱敏
     */
    @Test
    public void desensitizeCollection() {
        List<String> collection = new ArrayList<>(Arrays.asList("123456@qq.com", "1234567@qq.com", "12345678@qq.com"));

        // 使用Lambda表达式
        Collection<String> emails1 = SensitiveUtil.
                desensitizeCollection(collection,
                        (@EmailSensitive String value) -> {
                        });
        log.info("after使用Lambda表达式指定敏感信息描述者:{}", emails1);

        // 使用匿名内部类
        Collection<String> emails2 = SensitiveUtil.
                desensitizeCollection(new ArrayList<>(Arrays.asList("123456@qq.com", "1234567@qq.com", "12345678@qq.com")),
                        new SensitiveDescriptor<String>() {
                            @Override
                            public void describe(@EmailSensitive String value) {

                            }
                        });
        log.info("after使用匿名类指定敏感信息描述者:{}", emails2);
    }

    /**
     * 数组内部值脱敏
     */
    @Test
    public void desensitizeArray() {

        // 使用Lambda表达式
        String[] emails1 = SensitiveUtil.desensitizeArray(new String[]{"123456@qq.com", "1234567@qq.com", "12345678@qq.com"},
                (@EmailSensitive String value) -> {
                });
        log.info("after使用Lambda表达式指定敏感信息描述者:{}", Arrays.toString(emails1));

        // 使用匿名内部类
        String[] emails2 = SensitiveUtil.
                desensitizeArray(new String[]{"123456@qq.com", "1234567@qq.com", "12345678@qq.com"},
                        new SensitiveDescriptor<String>() {
                            @Override
                            public void describe(@EmailSensitive String element) {

                            }
                        });
        log.info("after使用匿名类指定敏感信息描述者:{}", Arrays.toString(emails2));
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

        log.info("before:{}", map);

        // 使用Lambda表达式
        Map<String, String> emails1 = SensitiveUtil.desensitizeMap(map,
                (@ChineseNameSensitive String key, @EmailSensitive String value) -> {
                });
        log.info("after使用Lambda表达式指定敏感信息描述者:{}", emails1);


        // 使用匿名内部类
        Map<String, String> emails2 = SensitiveUtil.
                desensitizeMap(map,
                        new MapSensitiveDescriptor<String, String>() {
                            @Override
                            public void describe(@ChineseNameSensitive String key, @EmailSensitive String value) {

                            }
                        });
        log.info("after使用匿名类指定敏感信息描述者:{}", emails2);
    }

    @Test
    public void printStackTrace() {
        log.info(CallerUtil.getCaller().toString());
        CallerUtil.printStackTrace();
    }

    @Test
    public void t() throws Exception {
        Field[] declaredFields = P.class.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>" + field.getName() + ":" + field.getAnnotatedType().getClass());
            AnnotatedType annotatedType = field.getAnnotatedType();
            if (annotatedType instanceof AnnotatedParameterizedType) {
                AnnotatedType[] annotatedActualTypeArguments = ((AnnotatedParameterizedType) annotatedType).getAnnotatedActualTypeArguments();
                Arrays.stream(annotatedActualTypeArguments).forEach(type -> System.out.println(Arrays.toString(type.getDeclaredAnnotations())));
            } else if (annotatedType instanceof AnnotatedTypeVariable) {
                AnnotatedType[] annotatedBounds = ((AnnotatedTypeVariable) annotatedType).getAnnotatedBounds();
                Arrays.stream(annotatedBounds).forEach(type -> System.out.println(Arrays.toString(type.getDeclaredAnnotations())));
            } else if (annotatedType instanceof AnnotatedArrayType) {
                AnnotatedType annotatedGenericComponentType = ((AnnotatedArrayType) annotatedType).getAnnotatedGenericComponentType();
                System.out.println(Arrays.toString(annotatedGenericComponentType.getDeclaredAnnotations()));
                System.out.println(Arrays.toString(annotatedType.getDeclaredAnnotations()));
            } else {
                System.out.println(Arrays.toString(annotatedType.getDeclaredAnnotations()));
            }

            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>" + field.getName() + ":" + field.getAnnotatedType().getClass());
        }
    }

    static class P<@EmailSensitive T extends @EmailSensitive(placeholder = '#') Number> {
        private @EmailSensitive T a;

        private List<@EmailSensitive T> b;

        private Map<@EmailSensitive String, @EmailSensitive T> c;

        private String d;

        private List<? extends T> e;

        private T@EmailSensitive [] f;

        private List g;

    }


}
