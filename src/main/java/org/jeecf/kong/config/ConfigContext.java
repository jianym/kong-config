package org.jeecf.kong.config;

/**
 * 配置上下文
 * 
 * @author jianyiming
 *
 */
public class ConfigContext {

    private static ConfigContext CONFIG_CONTEXT = null;

    private ConfigContext() {
    }

    public static ConfigContext getInstance() {
        if (CONFIG_CONTEXT != null) {
            return CONFIG_CONTEXT;
        }
        synchronized (ConfigContext.class) {
            if (CONFIG_CONTEXT != null) {
                return CONFIG_CONTEXT;
            }
            return CONFIG_CONTEXT = new ConfigContext();
        }
    }

    private AbstractConfigRefresh configRefresh = null;

    public void set(AbstractConfigRefresh configRefresh) {
        this.configRefresh = configRefresh;
    }

    public AbstractConfigRefresh getConfigRefresh() {
        return this.configRefresh;
    }

}
