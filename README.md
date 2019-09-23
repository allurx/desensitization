# desensitization
敏感信息擦除框架，用来擦除对象中的敏感信息。

# 用法
## maven依赖
```xml
<dependency>
  <groupId>red.zyc</groupId>
  <artifactId>desensitization</artifactId>
  <version>1.1.5</version>
</dependency>
```
## 例子
下面是一个Person类，其中包含了一些敏感字段
```java
public class Person {
    
    @ChineseNameSensitive
    String name = "张婷婷";

    @PhoneNumberSensitive
    String phoneNumber = "12345678912";

    @IdCardNumberSensitive
    String idCardNumber = "321181199301096002";
}
```
在数据入库或者前端返回时可能需要对相应的敏感字段进行脱敏处理，只需要调用以下方法即可擦除对象中的敏感信息。
```java
SensitiveUtil.handle(new Person());
```
更详细的例子可以参考[测试用例](https://github.com/Allurx/desensitization/blob/master/src/test/java/red/zyc/desensitization/Example.java)
