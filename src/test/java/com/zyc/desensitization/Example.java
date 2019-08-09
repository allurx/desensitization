package com.zyc.desensitization;

import com.zyc.desensitization.model.Child;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author zyc
 */
@Slf4j
public class Example {

    @Test
    public void test() {
        Child child = new Child();
        log.info(child.toString());
        SensitiveUtil.handle(child);
        log.info(child.toString());
    }
}
