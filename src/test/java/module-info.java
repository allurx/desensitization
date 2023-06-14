/**
 * @author zyc
 */
module desensitization.test {
    opens red.zyc.desensitization.test;
    opens red.zyc.desensitization.test.model;
    requires desensitization;
    requires annotation.parser;
    requires org.junit.jupiter;
}