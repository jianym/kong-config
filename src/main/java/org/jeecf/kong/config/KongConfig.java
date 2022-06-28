package org.jeecf.kong.config;

import org.jeecf.common.lang.StringUtils;
import org.jeecf.kong.config.server.DataSourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jianyiming
 *
 */
@Slf4j
@Component
@ComponentScan(basePackages = { "org.jeecf.kong.config" })
public class KongConfig implements ApplicationListener<ContextRefreshedEvent>, Ordered {

    @Autowired
    private DataSourceManager dataSourceManager;
    
    @Autowired
    private KongConfigProperties properties;

    private volatile boolean isEntry = false;

    @Override
    public int getOrder() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (isEntry()) {
            return;
        }
        try {
            if(properties.getZookeeper() != null && StringUtils.isNotEmpty(properties.getZookeeper().getAddress())) {
                dataSourceManager.setDataSource(DataSource.ZOOKEEPER.getCode());
            } else if(properties.getEtcd() != null && StringUtils.isNotEmpty(properties.getEtcd().getAddress())){
                dataSourceManager.setDataSource(DataSource.ETCD.getCode());
            } else {
                throw new RuntimeException("datasource is not exist");
            }
            String[] beans = event.getApplicationContext().getBeanDefinitionNames();
            for (int i = 0; i < beans.length; i++) {
                Object o = event.getApplicationContext().getBean(beans[i]);
                Class<?> clazz = o.getClass();
                EnableKongConfig config = AnnotationUtils.findAnnotation(clazz, EnableKongConfig.class);
                if (config != null && config.configRefresh() != null) {
                    Class<? extends AbstractConfigRefresh> configRefreshClass = config.configRefresh();
                    AbstractConfigRefresh configRefresh = configRefreshClass.newInstance();
                    ConfigContext.getInstance().set(configRefresh);
                }
            }
            dataSourceManager.listener("/");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(0);
        }
    }

    public boolean isEntry() {
        if (isEntry) {
            return isEntry;
        }
        synchronized (KongConfig.class) {
            if (isEntry) {
                return isEntry;
            }
            isEntry = true;
            return false;
        }
    }

}
