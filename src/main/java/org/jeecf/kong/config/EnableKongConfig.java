package org.jeecf.kong.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

/**
 * 配置中心启动
 * 
 * @author jianyiming
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Import(KongConfig.class)
public @interface EnableKongConfig {

    /**
     * 配置刷新类
     * 
     * @return 具体实现类
     */
    public Class<? extends AbstractConfigRefresh> configRefresh() default BasicConfigRefresh.class;

}
