package cn.raocloud.annotation;

import java.lang.annotation.*;

/**
 * @ClassName: RepeatSubmit
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/12/2 13:18
 * @Version 1.0
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {
}
