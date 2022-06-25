package org.jeecf.kong.config;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

/**
 * 配置动态刷新
 * 
 * @author jianyiming
 *
 */
public abstract class AbstractConfigRefresh {

    private Set<String> keys = whiteList();

    public void chain(String path, String value) {
        String key = this.resolve(path);
        if (this.filter(key)) {
            this.refresh(key, value);
        }
    }

    /**
     * 过滤key,满足set要求的key将会进入
     * 
     * @param key
     */
    public boolean filter(String key) {
        if (CollectionUtils.isEmpty(keys))
            return true;
        return this.keys.contains(key);
    }

    /**
     * 刷新配置上下文
     * 
     * @param key
     * @param value
     */
    public abstract void refresh(String key, String value);

    /**
     * key 白名单,如果为空，全部通过
     * 
     * @return 返回白名单列表
     */
    public abstract Set<String> whiteList();

    /**
     * 解析path to key
     * 
     * @param path
     * @return 返回转换后参数
     */
    public abstract String resolve(String path);

}
