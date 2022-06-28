package org.jeecf.kong.config.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecf.kong.config.EdClient;
import org.jeecf.kong.config.KongConfigProperties;
import org.jeecf.kong.config.MultiConfigEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.ByteString;
import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.api.RangeResponse;

/**
 * etcd 配置服务
 * 
 * @author jianyiming
 *
 */
@Service
public class EtcdConfigService implements ConfigInfoServer {

    @Autowired
    private KongConfigProperties properties;

    @Override
    public MultiConfigEntity query(String path) throws Exception {
        if (StringUtils.isEmpty(path)) {
            path = "/";
        }
        RangeResponse res = EdClient.getInstance(properties.getEtcd()).getAsPrefix(path);
        MultiConfigEntity root = new MultiConfigEntity();
        root.setPath(path);
        root.setRootPath(path);
        if (res != null) {
            List<KeyValue> kvList = res.getKvsList();
            if (CollectionUtils.isNotEmpty(kvList)) {
                buildTree(root, EdClient.getNamespace() + path, kvList);
            }
        }
        return root;
    }

    public void buildTree(MultiConfigEntity rootEntity, String rootPath, List<KeyValue> kvList) {
        if (!rootPath.startsWith("/")) {
            rootPath = "/" + rootPath;
        }
        if (rootPath.endsWith("/")) {
            rootPath = rootPath.substring(0, rootPath.length() - 1);
        }
        Map<String, MultiConfigEntity> entityMap = new HashMap<>();
        for (KeyValue kv : kvList) {
            String childKey = kv.getKey().toStringUtf8();
            String childValue = kv.getValue().toStringUtf8();
            int parentIndex = childKey.lastIndexOf("/");
            String parentKey = childKey.substring(0, parentIndex);
            MultiConfigEntity parentEntity = entityMap.get(parentKey);
            MultiConfigEntity children = new MultiConfigEntity();
            children.setParent(parentKey);
            children.setRootPath(childKey);
            children.setPath(childKey.substring(parentIndex + 1, childKey.length()));
            children.setValue(childValue);
            if (parentEntity != null) {
                List<MultiConfigEntity> childrenList = parentEntity.getChildrens();
                if (childrenList == null) {
                    childrenList = new ArrayList<>();
                    parentEntity.setChildrens(childrenList);
                }
                childrenList.add(children);
            } else {
                List<MultiConfigEntity> childrenList = rootEntity.getChildrens();
                if (childrenList == null) {
                    childrenList = new ArrayList<>();
                    rootEntity.setChildrens(childrenList);
                }
                childrenList.add(children);
            }
            entityMap.put(childKey, children);
        }

    }

    @Override
    public String add(String path, String value) throws Exception {
        String tempPath = path.substring(0, path.lastIndexOf("/"));
        if (StringUtils.isEmpty(tempPath)) {
            tempPath = "";
        }
        RangeResponse rangRes = EdClient.getInstance(properties.getEtcd()).get(tempPath);
        if (rangRes != null && CollectionUtils.isNotEmpty(rangRes.getKvsList())) {
            ByteString tempValue = rangRes.getKvs(0).getValue();

            if (tempValue != null && tempValue.size() > 0) {
                throw new RuntimeException("叶子节点不能添加子节点");
            }
        }
        EdClient.getInstance(properties.getEtcd()).put(path, value);
        return path;
    }

    @Override
    public void remove(String path) throws Exception {
        EdClient.getInstance(properties.getEtcd()).delete(path);
    }

    @Override
    public String update(String path, String value) throws Exception {
        RangeResponse res = EdClient.getInstance(properties.getEtcd()).getAsPrefix(path);
        if (res != null && CollectionUtils.isNotEmpty(res.getKvsList()) && res.getKvsList().size() > 1) {
            throw new RuntimeException("索引节点不能更新");
        }
        EdClient.getInstance(properties.getEtcd()).update(path, value);
        return path;
    }

    @Override
    public void listener(String rootPath) throws Exception {
        EdClient.getInstance(properties.getEtcd()).watch();
    }

}
