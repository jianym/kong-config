package org.jeecf.kong.config;

import java.util.Set;

import org.jeecf.kong.config.common.SpringContextUtils;
import org.springframework.cloud.context.refresh.ContextRefresher;

/**
 * 配置刷新类
 * 
 * @author jianyiming
 *
 */
public class BasicConfigRefresh extends AbstractConfigRefresh {

    @Override
    public void refresh(String key, String value) {
        ContextRefresher contextRefresher = SpringContextUtils.getBean(ContextRefresher.class);
        if (value != null) {
            System.setProperty(key, value);
            contextRefresher.refresh();
        }
    }

    @Override
    public Set<String> whiteList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String resolve(String path) {
        String[] nodes = path.split("/");
        StringBuilder result = new StringBuilder(nodes[1]);
        for (int i = 2; i < nodes.length; i++) {
            result.append(".");
            result.append(nodes[i]);
        }
        return result.toString();
    }

}
