package org.jeecf.kong.config.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.jeecf.kong.config.ConfigContext;
import org.springframework.stereotype.Component;

/**
 * zk监听者
 * 
 * @author jianyiming
 *
 */
@Component
public class ZkListener {

    public void load(String rootPath, CuratorFramework curator) throws Exception {
        watchServer(rootPath, curator);
    }

    private void watchServer(String rootPath, CuratorFramework curator) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curator, rootPath, true);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                PathChildrenCacheEvent.Type type = pathChildrenCacheEvent.getType();
                if (type.equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                    byte[] source = pathChildrenCacheEvent.getData().getData();
                    if (source != null && source.length > 0) {
                        String data = new String(source);
                        String path = pathChildrenCacheEvent.getData().getPath();
                        ConfigContext.getInstance().getConfigRefresh().chain(path,  data);
                    }
                } else if (type.equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {
                    byte[] source = pathChildrenCacheEvent.getData().getData();
                    if (source != null && source.length > 0) {
                        String data = new String(source);
                        String path = pathChildrenCacheEvent.getData().getPath();
                        ConfigContext.getInstance().getConfigRefresh().chain(path,  data);
                    }
                }
            }
        });
        pathChildrenCache.start();
    }

}
