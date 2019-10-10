# desensitization
敏感信息擦除框架，用来擦除包含敏感信息的对象或者敏感者。

# 用法
## jdk版本
1.8
## maven依赖
```xml
<dependency>
  <groupId>red.zyc</groupId>
  <artifactId>desensitization</artifactId>
  <version>2.0.0</version>
</dependency>
```
## 例子
### 擦除对象内部的敏感信息
下面是一个Person类，其中包含了一些敏感字段以及一些嵌套的需要擦除敏感信息的域
```java
public class Person {
    
    @ChineseNameSensitive
    String name = "张婷婷";

    @PhoneNumberSensitive
    String phoneNumber = "12345678912";

    @IdCardNumberSensitive
    String idCardNumber = "321181199301096002";
    
    @EraseSensitive
    Boy boy = new Boy();
    
    @EraseSensitive
    List<Girl> girls = new ArrayList<>(Arrays.asList(new Girl(), new Girl()));
}
```
在数据入库或者前端返回时可能需要对相应的敏感字段进行脱敏处理，只需要调用以下方法即可擦除对象中的敏感信息。
```java
Person person = SensitiveUtil.desensitize(new Person());
```
### 单个值脱敏
可能你的敏感信息是一个字符串类型的值，只需要调用另一个重载的方法即可擦除**单个值类型的敏感信息**。
```java
String email = SensitiveUtil.desensitize("123456@qq.com", (@EmailSensitive String value) -> false);
```
当然了这里的**单个值**既可以是集合也可以是数组。更详细的例子可以参考：
[测试用例](https://github.com/Allurx/desensitization/blob/master/src/test/java/red/zyc/desensitization/Example.java)
