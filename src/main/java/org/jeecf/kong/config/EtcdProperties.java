package org.jeecf.kong.config;

import lombok.Data;

/**
 * etcd 配置信息
 * 
 * @author jianyiming
 *
 */
@Data
public class EtcdProperties {

    /**
     * 地址
     */
    private String address;
    /**
     * 命名空间
     */
    private String namespace;
    /**
     * 验证
     */
    private String username;
    /**
     * 验证
     */
    private String password;
    /**
     * session 超时
     */
    private Integer sessionTimeout = 5000;
    /**
     * 连接 超时
     */
    private Integer connectTimeout = 3000;

}
