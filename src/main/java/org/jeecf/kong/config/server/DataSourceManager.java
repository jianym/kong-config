package org.jeecf.kong.config.server;

import org.jeecf.kong.config.DataSource;
import org.jeecf.kong.config.MultiConfigEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 数据源操作
 * 
 * @author jianyiming
 *
 */
@Component
public class DataSourceManager implements ConfigInfoServer {

    @Autowired
    private ZookeeperConfigService zookeeperConfigService;

    private volatile int dataSource = DataSource.ZOOKEEPER.getCode();

    public void setDataSource(int dataSource) {
        if (dataSource == DataSource.ZOOKEEPER.getCode())
            this.dataSource = DataSource.ZOOKEEPER.getCode();
    }

    @Override
    public MultiConfigEntity query(String path) throws Exception {
        if (this.dataSource == DataSource.ZOOKEEPER.getCode()) {
            return zookeeperConfigService.query(path);
        }
        return null;
    }

    @Override
    public String add(String path, String value) throws Exception {
        if (this.dataSource == DataSource.ZOOKEEPER.getCode()) {
            return zookeeperConfigService.add(path, value);
        }
        return null;
    }

    @Override
    public void remove(String path) throws Exception {
        if (this.dataSource == DataSource.ZOOKEEPER.getCode()) {
            zookeeperConfigService.remove(path);
        }
    }

    @Override
    public String update(String path, String value) throws Exception {
        if (this.dataSource == DataSource.ZOOKEEPER.getCode()) {
            return zookeeperConfigService.update(path, value);
        }
        return null;
    }

    @Override
    public void listener(String rootPath) throws Exception {
        if (this.dataSource == DataSource.ZOOKEEPER.getCode()) {
            zookeeperConfigService.listener(rootPath);
        }

    }

}
