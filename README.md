# desensitization

基于Java反射api、简单易用、支持各种复杂数据结构的数据脱敏库，包含但不限于以下类型的数据脱敏

* **邮箱**
* **手机号码**
* **中文名称**
* **身份证号码**
* **银行卡号码**
* **密码**
* **级联脱敏**
* **自定义注解脱敏**

# 用法
## JDK版本
desensitization是基于JDK21开发的，JDK1.8及以上版本请参考该[使用指南](https://github.com/allurx/desensitization/tree/v2.4.6)

## maven依赖

```xml

<dependency>
    <groupId>red.zyc</groupId>
    <artifactId>desensitization</artifactId>
    <version>3.0.0</version>
</dependency>
```

## 例子

### 对象域值脱敏

下面是一个Child类，其中包含了一些敏感域以及一些嵌套的敏感域

```java
public class Child {

    @ChineseName
    public String name = "小明";

    @PhoneNumber
    public String phoneNumber = "19962000001";

    @Password
    public String password = "123456789";

    @Cascade
    public Father father;    

}
```

只需要在敏感数据字段上标记相应类型的敏感注解，例如`@ChineseName`、`@Password`等注解，
如果这个数据字段是需要级联脱敏的对象，你只需要在该字段上标注`@Cascade`注解，
最后调用以下方法即可擦除对象中的所有敏感信息然后返回一个新的Child对象。

```java
var child = Sensitive.desensitize(new Child());
```

### 值脱敏

可能你的敏感信息是一个字符串类型的值或者是一个`Collection`、`Array`、`Map`之类的值，同样擦除它们的敏感信息也很简单

```java
void desensitize(){

        // String
        System.out.printf("字符串脱敏: %s%n",Sensitive.desensitize("123456@qq.com",new AnnotatedTypeToken<@Email String>(){
        }));

        // Collection
        System.out.printf("集合脱敏: %s%n",Sensitive.desensitize(Stream.of("123456@qq.com","1234567@qq.com","1234568@qq.com").collect(Collectors.toList()),
        new AnnotatedTypeToken<List<@Email String>>(){
        }));

        // Array
        System.out.printf("数组脱敏: %s%n", Arrays.toString(Sensitive.desensitize(new String[]{"123456@qq.com","1234567@qq.com","12345678@qq.com"},
        new AnnotatedTypeToken<@Email String[]>(){
        })));

        // Map
        System.out.printf("Map脱敏: %s%n",Sensitive.desensitize(Stream.of("张三","李四","小明").collect(Collectors.toMap(s->s, s->"123456@qq.com")),
        new AnnotatedTypeToken<Map<@ChineseName String, @Email String>>(){
        }));
}
```
在上面的例子中我们只需要构造脱敏对象的`AnnotatedTypeToken`以便我们能够准确的捕获被脱敏对象的实际类型和相应的敏感注解。
# 原理

desensitization库是基于Java1.8新增的AnnotatedType这种新的类型体系来解析各种复杂数据结构中的脱敏注解，然后通过责任链这种设计模式完成数据脱敏处理的，要想完全理解其背后的实现原理需要对Java的Type体系和AnnotatedType体系有较为深刻的理解，可以参考以下几篇文章了解Java中的类型体系以及注解体系。

* [Java Type](https://www.zyc.red/Java/Reflection/Type)
* [Java AnnotatedType](https://www.zyc.red/Java/Reflection/AnnotatedType)
* [Java AnnotatedElement](https://www.zyc.red/Java/Reflection/AnnotatedElement)

# 扩展

如果你的应用是基于spring-boot搭建的，并且你不想在代码中每次都手动调用脱敏方法对数据进行脱敏处理，那么[desensitization-spring-boot](https://github.com/allurx/desensitization-spring-boot)
这个starter可能会对你有很大的帮助，详细的信息可以查看该工程介绍。

# License

[Apache License 2.0](https://github.com/allurx/desensitization/blob/master/LICENSE.txt)
