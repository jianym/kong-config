package org.jeecf.kong.config;

import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecf.kong.config.listener.EtcdListener;

import com.google.protobuf.ByteString;
import com.ibm.etcd.api.DeleteRangeResponse;
import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.api.PutResponse;
import com.ibm.etcd.api.RangeResponse;
import com.ibm.etcd.client.EtcdClient;
import com.ibm.etcd.client.kv.KvClient;

/**
 * etcd 客户端
 * 
 * @author jianyiming
 *
 */
public class EdClient {

    private static volatile EdClient edClient = null;

    private static KvClient kvClient = null;

    private static String namespace = null;

    /**
     * @param endPoints 如https://127.0.0.1:2379 有多个时逗号分隔
     */
    private static void init(EtcdProperties etcdProperties) {
        if (StringUtils.isEmpty(etcdProperties.getNamespace())) {
            throw new RuntimeException("etcd namespace is empty");
        }
        EtcdClient.Builder builder = EtcdClient.forEndpoints(etcdProperties.getAddress()).withDefaultTimeout(etcdProperties.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .withSessionTimeoutSecs(etcdProperties.getSessionTimeout()).withPlainText();
        if (StringUtils.isNotEmpty(etcdProperties.getUsername()) && StringUtils.isNotEmpty(etcdProperties.getPassword())) {
            builder.withCredentials(ByteString.copyFromUtf8(etcdProperties.getUsername()), ByteString.copyFromUtf8(etcdProperties.getPassword()));
        }
        EdClient.kvClient = builder.build().getKvClient();
        EdClient.namespace = "/kong-config/" + etcdProperties.getNamespace();
    }

    public static String getNamespace() {
        return EdClient.namespace;
    }

    private EdClient() {
    }

    public static EdClient getInstance(EtcdProperties etcdProperties) {
        System.out.println();
        if (edClient != null) {
            return edClient;
        }
        synchronized (EdClient.class) {
            if (edClient != null) {
                return edClient;
            }
            EdClient.init(etcdProperties);
            return edClient = new EdClient();
        }
    }

    public PutResponse put(String key, String value) {
        if (!key.startsWith(namespace)) {
            key = namespace + key;
        }
        return kvClient.put(ByteString.copyFromUtf8(key), ByteString.copyFromUtf8(value)).sync();
    }

    public RangeResponse getAsPrefix(String key) {
        if (!key.startsWith(namespace)) {
            key = namespace + key;
        }
        return kvClient.get(ByteString.copyFromUtf8(key)).asPrefix().sync();
    }

    public RangeResponse get(String key) {
        if (!key.startsWith(namespace)) {
            key = namespace + key;
        }
        return kvClient.get(ByteString.copyFromUtf8(key)).sync();
    }

    public PutResponse update(String key, String value) {
        if (!key.startsWith(namespace)) {
            key = namespace + key;
        }
        return kvClient.setValue(ByteString.copyFromUtf8(key), ByteString.copyFromUtf8(value)).sync();
    }

    public DeleteRangeResponse delete(String key) {
        if (StringUtils.isNotEmpty(key)) {
            if (!key.startsWith(namespace)) {
                key = namespace + key;
            }
            return kvClient.delete(ByteString.copyFromUtf8(key)).sync();
        }
        return null;
    }

    public void watch() {
        RangeResponse rangRes = this.getAsPrefix(namespace);
        if (rangRes != null && CollectionUtils.isNotEmpty(rangRes.getKvsList())) {
            for (KeyValue kv : rangRes.getKvsList()) {
                String key = kv.getKey().toStringUtf8();
                if (!key.equals(EdClient.getNamespace()) && !key.equals(EdClient.getNamespace() + "/")) {
                    String value = kv.getValue().toStringUtf8();
                    key = key.substring(EdClient.getNamespace().length());
                    ConfigContext.getInstance().getConfigRefresh().chain(key, value);
                }
            }
        }
        kvClient.watch(ByteString.copyFromUtf8(namespace)).asPrefix().start(new EtcdListener());
    }

}
