# desensitization

基于Java反射api、简单易用、支持任意复杂数据结构的数据脱敏库，包含但不限于以下类型的数据脱敏

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

下面是一个Child类，其中包含了一些敏感数据字段以及一些嵌套的敏感数据字段

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
如果这个数据字段是需要级联脱敏的对象，只需要在该字段上标注`@Cascade`注解，
最后调用以下方法即可擦除对象中的所有敏感信息并返回一个新的Child对象。

```java
var child = Sensitive.desensitize(new Child());
```

### 值脱敏

可能你的敏感信息是一个字符串类型的值或者是一个`Collection`、`Array`、`Map`之类的值，同样擦除它们的敏感信息也很简单

```java
void desensitize() {

    // String
    var v1 = Sensitive.desensitize("123456@qq.com", new AnnotatedTypeToken<@Email String>() {
    });
    assert "1*****@qq.com".equals(v1);

    // Collection
    var v2 = Sensitive.desensitize(Stream.of("123456@qq.com").collect(Collectors.toList()), new AnnotatedTypeToken<List<@Email String>>() {
    });
    v2.forEach(s -> {
        assert "1*****@qq.com".equals(s);
    });

    // Array
    var v3 = Sensitive.desensitize(new String[]{"123456@qq.com"}, new AnnotatedTypeToken<@Email String[]>() {
    });
    Arrays.stream(v3).forEach(s -> {
        assert "1*****@qq.com".equals(s);
    });

    // Map
    var v4 = Sensitive.desensitize(Stream.of("张三").collect(Collectors.toMap(s -> s, s -> "123456@qq.com")), new AnnotatedTypeToken<Map<@ChineseName String, @Email String>>() {
    });
    v4.forEach((s1, s2) -> {
        assert "张*".equals(s1);
        assert "1*****@qq.com".equals(s2);
    });
}
```
在上面的例子中我们只需要构造脱敏对象的`AnnotatedTypeToken`以便我们能够准确的捕获被脱敏对象的实际类型和相应的敏感注解。
# 原理

desensitization是基于[annotation-parser](https://github.com/allurx/annotation-parser)库来解析各种复杂数据结构中自定义敏感注解的，详细的信息可以查看该工程介绍。

# 扩展

如果你的应用是基于spring-boot搭建的，并且你不想在代码中每次都手动调用脱敏方法对数据进行脱敏处理，那么[desensitization-spring-boot](https://github.com/allurx/desensitization-spring-boot)
这个starter可能会对你有很大的帮助，详细的信息可以查看该工程介绍。

# License

[Apache License 2.0](https://github.com/allurx/desensitization/blob/master/LICENSE.txt)
