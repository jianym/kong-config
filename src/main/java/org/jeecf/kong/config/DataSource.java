package org.jeecf.kong.config;

/**
 * 数据源枚举
 * 
 * @author jianyiming
 *
 */
public enum DataSource {

    ZOOKEEPER(1), ETCD(2);

    private int code;

    DataSource(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

}
