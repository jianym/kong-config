package org.jeecf.kong.config.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.jeecf.kong.config.KongConfigProperties;
import org.jeecf.kong.config.MultiConfigEntity;
import org.jeecf.kong.config.ZkClient;
import org.jeecf.kong.config.listener.ZkListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * zk 配置服务
 * 
 * @author jianyiming
 *
 */
@Service
public class ZookeeperConfigService implements ConfigInfoServer {

    @Autowired
    private KongConfigProperties properties;

    @Autowired
    private ZkListener zkListener;

    @Override
    public MultiConfigEntity query(String path) throws Exception {
        if (StringUtils.isEmpty(path)) {
            path = "/";
        }

        CuratorFramework curator = ZkClient.getSingleCuratorFramework(properties.getZookeeper());
        MultiConfigEntity entity = new MultiConfigEntity();
        entity.setPath(path);
        entity.setRootPath(path);
        query(curator, entity, path);
        return entity;
    }

    private void query(CuratorFramework curator, MultiConfigEntity entity, String rootPath) throws Exception {
        if (!rootPath.startsWith("/")) {
            rootPath = "/" + rootPath;
        }
        byte[] value = ZkClient.get(curator, rootPath);
        if (value != null) {
            entity.setValue(new String(value));
        }
        List<String> pathList = ZkClient.children(curator, rootPath);
        if (CollectionUtils.isNotEmpty(pathList)) {
            for (int i = 0; i < pathList.size(); i++) {
                List<MultiConfigEntity> childrens = entity.getChildrens();
                MultiConfigEntity child = new MultiConfigEntity();
                child.setPath(pathList.get(i));
                child.setParent(rootPath);
                String tmpRootPath = null;
                if (rootPath.equals("/"))
                    tmpRootPath = rootPath + pathList.get(i);
                else
                    tmpRootPath = rootPath + "/" + pathList.get(i);
                child.setRootPath(tmpRootPath);
                if (childrens == null) {
                    childrens = new ArrayList<>();
                    entity.setChildrens(childrens);
                }
                childrens.add(child);
                query(curator, child, tmpRootPath);
            }
        }
    }

    @Override
    public String add(String path, String value) throws Exception {
        CuratorFramework curator = ZkClient.getSingleCuratorFramework(properties.getZookeeper());
        ZkClient.create(curator, CreateMode.PERSISTENT, path, value.getBytes());
        return path;
    }

    @Override
    public void remove(String path) throws Exception {
        CuratorFramework curator = ZkClient.getSingleCuratorFramework(properties.getZookeeper());
        ZkClient.remove(curator, path);
    }

    @Override
    public String update(String path, String value) throws Exception {
        CuratorFramework curator = ZkClient.getSingleCuratorFramework(properties.getZookeeper());
        ZkClient.set(curator, path, value.getBytes());
        return path;
    }

    @Override
    public void listener(String rootPath) throws Exception {
        CuratorFramework curator = ZkClient.getSingleCuratorFramework(properties.getZookeeper());
        zkListener.load(rootPath, curator);
        listener(rootPath, curator);
    }

    private void listener(String rootPath, CuratorFramework curator) throws Exception {
        List<String> childrens = ZkClient.children(curator, rootPath);
        if (CollectionUtils.isNotEmpty(childrens)) {
            for (int i = 0; i < childrens.size(); i++) {
                String tmpRootPath = null;
                if (rootPath.equals("/"))
                    tmpRootPath = rootPath + childrens.get(i);
                else
                    tmpRootPath = rootPath + "/" + childrens.get(i);
                zkListener.load(tmpRootPath, curator);
                listener(tmpRootPath, curator);
            }
        }
    }

}
