package org.jeecf.kong.config;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.springframework.util.StringUtils;

/**
 * zk 客服端
 * 
 * @author jianyiming
 *
 */
public class ZkClient {

    public static final String SEVER_PATH = "server";

    private volatile static CuratorFramework SINGLE_CURATOR = null;

    private static CuratorFramework initClient(ZookeeperProperties zkProperties) {
        RetryNTimes retryNTimes = new RetryNTimes(zkProperties.getRetry(), 3000);
        Builder builder = CuratorFrameworkFactory.builder();
        builder.connectString(zkProperties.getAddress()).sessionTimeoutMs(zkProperties.getSessionTimeout()).connectionTimeoutMs(zkProperties.getConnectTimeout())
                .namespace(zkProperties.getNamespace() + "/kong-config").retryPolicy(retryNTimes);
        if (!StringUtils.isEmpty(zkProperties.getAuth()) && !StringUtils.isEmpty(zkProperties.getScheme()))
            builder.authorization(zkProperties.getScheme(), zkProperties.getAuth().getBytes());
        return builder.build();
    }

    public static CuratorFramework getSingleCuratorFramework(ZookeeperProperties zkProperties) {
        if (SINGLE_CURATOR != null)
            return SINGLE_CURATOR;
        synchronized (ZkClient.class) {
            if (SINGLE_CURATOR != null)
                return SINGLE_CURATOR;
            SINGLE_CURATOR = initClient(zkProperties);
            SINGLE_CURATOR.start();
        }
        return SINGLE_CURATOR;
    }

    public static CuratorFramework getSingleCuratorFramework() {
        return SINGLE_CURATOR;
    }

    public static String creatingParentsIfNeeded(CuratorFramework curator, CreateMode mode, String path, byte[] data) throws Exception {
        return curator.create().creatingParentContainersIfNeeded().withMode(mode).forPath(path, data);
    }

    public static String create(CuratorFramework curator, CreateMode mode, String path, byte[] data) throws Exception {
        return curator.create().withMode(mode).forPath(path, data);
    }

    public static String create(CuratorFramework curator, CreateMode mode, String path) throws Exception {
        return curator.create().withMode(mode).forPath(path);
    }

    public static byte[] get(CuratorFramework curator, String path) throws Exception {
        return curator.getData().forPath(path);
    }

    public static void set(CuratorFramework curator, String path, byte[] data) throws Exception {
        curator.setData().forPath(path, data);
    }

    public static List<String> children(CuratorFramework curator, String path) throws Exception {
        return curator.getChildren().forPath(path);
    }

    public static void remove(CuratorFramework curator, String path) throws Exception {
        curator.delete().forPath(path);
    }

}
