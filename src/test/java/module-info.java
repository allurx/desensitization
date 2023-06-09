/**
 * @author zyc
 */
module desensitization.test {
    opens red.zyc.desensitization.test;
    opens red.zyc.desensitization.test.model;
    requires org.junit.jupiter.api;
    requires desensitization;
    requires annotation.parser;
}