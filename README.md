# desensitization
基于java反射，在运行时动态擦除对象中的敏感信息。包含但不限于以下类型的数据脱敏：
* **邮箱**
* **手机号码**
* **中文名称**
* **身份证号码**
* **银行卡号码**
* **密码**
* **统一社会信用代码**
* **任意`CharSequence`类型的值**

# 用法
## jdk版本
大于等于1.8
## maven依赖
```xml
<dependency>
  <groupId>red.zyc</groupId>
  <artifactId>desensitization</artifactId>
  <version>2.2.8</version>
</dependency>
```
## 例子
### 对象域值脱敏
下面是一个Child类，其中包含了一些敏感域以及一些嵌套的敏感域
```java
public class Child {
    
    @ChineseNameSensitive
    private String name = "李富贵";

    @IdCardNumberSensitive
    private String idCardNumber = "321181199301096000";

    @UsccSensitive
    private String unifiedSocialCreditCode = "91310106575855456U";

    @CharSequenceSensitive
    private String string = "123456";

    @EmailSensitive
    private String email = "123456@qq.com";

    @PasswordSensitive
    private String password = "123456";

    @CascadeSensitive
    private Mother mother = new Mother();

    @CascadeSensitive
    private Father father = new Father();
    
    private @PasswordSensitive String[] passwords = {"123456", "1234567", "12345678"};

    private List<@CascadeSensitive Parent> parents1 = Stream.of(new Father(), new Mother()).collect(Collectors.toList());

    private List<@EmailSensitive String> emails1 = Stream.of("123456@qq.com", "1234567@qq.com", "1234568@qq.com").collect(Collectors.toList());
    
    private Map<@ChineseNameSensitive String, @EmailSensitive String> emails2 = Stream.of("张三", "李四", "小明").collect(Collectors.toMap(s -> s, s -> "123456@qq.com"));

}
```
在数据入库或者前端返回时可能需要对相应的敏感字段进行脱敏处理，你只需要在敏感数据字段上标记相应类型的敏感注解，例如`@ChineseNameSensitive`、`@EmailSensitive`等注解，如果你的数据字段是需要级联脱敏的对象，你只需要在该字段上标注`@CascadeSensitive`注解，最后调用以下方法即可擦除对象中的所有敏感信息然后返回一个新的Child对象，不会改变原对象中的域值。
```java
Child child = Sensitive.desensitize(new Child());
```
### 值脱敏
可能你的敏感信息是一个字符串类型的值或者是一个`Collection`、`Array`、`Map`之类的值，同样擦除它们的敏感信息也很简单：
```java
static void desensitizeValue(){
    // 单个值
    log.info("值脱敏：{}", Sensitive.desensitize("123456@qq.com", new TypeToken<@EmailSensitive String>() {
    }));

    // Collection
    log.info("集合值脱敏：{}", Sensitive.desensitize(Stream.of("123456@qq.com", "1234567@qq.com", "1234568@qq.com").collect(Collectors.toList()),
            new TypeToken<List<@EmailSensitive String>>() {
            }));

    // Array
    log.info("数组值脱敏：{}", Arrays.toString(Sensitive.desensitize(new String[]{"123456@qq.com", "1234567@qq.com", "12345678@qq.com"},
            new TypeToken<@EmailSensitive String[]>() {
            })));

    // Map
    log.info("Map值脱敏：{}", Sensitive.desensitize(Stream.of("张三", "李四", "小明").collect(Collectors.toMap(s -> s, s -> "123456@qq.com")),
            new TypeToken<Map<@ChineseNameSensitive String, @EmailSensitive String>>() {
            }));
}
```
在上面的例子中通过`TypeToken`指定需要脱敏对象的类型以便我们能够准确的捕获被脱敏对象的实际类型和相应的敏感注解。
这里有一个很重要的地方需要我们格外的关注：**由于jdk在解析注解时的bug导致无法正确的获取嵌套类上的注解，因此对于值脱敏时，我们需要将脱敏代码放到一个静态方法或者是静态块中，这样运行时才能正确的获取脱敏对象上的注解。**
有关这个bug的详情可以参考这个链接[why-annotation-on-generic-type-argument-is-not-visible-for-nested-type](http://stackoverflow.com/questions/39952812/why-annotation-on-generic-type-argument-is-not-visible-for-nested-type)
# 测试用例
更详细的例子可以参考
1. [一个需要脱敏的复杂对象](https://github.com/Allurx/desensitization/blob/master/src/test/java/red/zyc/desensitization/model/Child.java)
2. [测试用例](https://github.com/Allurx/desensitization/blob/master/src/test/java/red/zyc/desensitization/Example.java)
# License
[Apache License 2.0](https://github.com/Allurx/desensitization/blob/master/LICENSE.txt)
