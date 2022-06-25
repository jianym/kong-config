package org.jeecf.kong.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * kong-config 配置
 * 
 * @author jianyiming
 *
 */
@Data
@Component
@ConfigurationProperties(prefix = "kong.config")
public class KongConfigProperties {
    /**
     * zk配置
     */
    private ZookeeperProperties zookeeper;
    
}
