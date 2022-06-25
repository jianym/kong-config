package org.jeecf.kong.config;

import lombok.Data;

/**
 * zk配置
 * 
 * @author jianyiming
 *
 */
@Data
public class ZookeeperProperties {

    /**
     * 地址
     */
    private String address;
    /**
     * 命名空间
     */
    private String namespace;
    /**
     * 验证 scheme
     */
    private String scheme;
    /**
     * 验证
     */
    private String auth;
    /**
     * 重试
     */
    private Integer retry = 3;
    /**
     * session 超时
     */
    private Integer sessionTimeout = 5000;
    /**
     * 连接 超时
     */
    private Integer connectTimeout = 3000;

}
