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
import red.zyc.desensitization.util.CallerUtil;
import red.zyc.desensitization.util.ReflectionUtil;

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
                desensitizeCollection(collection,
                        (@EmailSensitive String value) -> {
                        });
        log.info("after使用Lambda表达式指定Collection类型敏感信息描述者:{}", emails1);

        // 使用匿名内部类
        Collection<String> emails2 = SensitiveUtil.
                desensitizeCollection(new ArrayList<>(Arrays.asList("123456@qq.com", "1234567@qq.com", "12345678@qq.com")),
                        new SensitiveDescriptor<String>() {
                            @Override
                            public void describe(@EmailSensitive String value) {

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
        String[] emails1 = SensitiveUtil.desensitizeArray(emails,
                (@EmailSensitive String value) -> {
                });
        log.info("after使用Lambda表达式指定Array类型敏感信息描述者:{}", Arrays.toString(emails1));

        // 使用匿名内部类
        String[] emails2 = SensitiveUtil.
                desensitizeArray(emails,
                        new SensitiveDescriptor<String>() {
                            @Override
                            public void describe(@EmailSensitive String element) {

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
        Map<String, String> emails1 = SensitiveUtil.desensitizeMap(map,
                (@ChineseNameSensitive String key, @EmailSensitive String value) -> {
                });
        log.info("after使用Lambda表达式指定Map类型敏感信息描述者:{}", emails1);


        // 使用匿名内部类
        Map<String, String> emails2 = SensitiveUtil.
                desensitizeMap(map,
                        new MapSensitiveDescriptor<String, String>() {
                            @Override
                            public void describe(@ChineseNameSensitive String key, @EmailSensitive String value) {

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
    public void t() throws Exception {
        for (Field field : P.class.getDeclaredFields()) {
            System.out.println(">>>>>>>>>>>>>>");
            field.setAccessible(true);
            AnnotatedType annotatedType = field.getAnnotatedType();
            //AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) field.getAnnotatedType();
            System.out.println(Arrays.toString(ReflectionUtil.getRawClass(annotatedType)));

            //Arrays.stream(annotatedParameterizedType.getAnnotatedActualTypeArguments()).forEach(annotatedType -> System.out.println(get(annotatedType.getType())));
            System.out.println(">>>>>>>>>>>>>>");
        }
    }

    static class P<E extends Integer & Collection<String>, T extends E> {

        List<String> d;
        List<List<@EmailSensitive String>> a;
        private T e;

        private E hh;

        private T[] a1;
        private E[] a2;
        private List<String>[] a3;

        List<? extends Collection<String>> c = new ArrayList<>();

        private String gg;

        //List<@EmailSensitive ?> b;

        //List<String[]> c;


    }

    static class B extends Number implements Collection<String> {

        @Override
        public int intValue() {
            return 0;
        }

        @Override
        public long longValue() {
            return 0;
        }

        @Override
        public float floatValue() {
            return 0;
        }

        @Override
        public double doubleValue() {
            return 0;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<String> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean add(String s) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends String> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }
    }

}
