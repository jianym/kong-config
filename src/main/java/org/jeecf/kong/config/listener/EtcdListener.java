package org.jeecf.kong.config.listener;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.jeecf.kong.config.ConfigContext;
import org.jeecf.kong.config.EdClient;

import com.ibm.etcd.api.Event;
import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.client.kv.WatchUpdate;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

/**
 * etcd 监听器
 * 
 * @author jianyiming
 *
 */
@Slf4j
public class EtcdListener implements StreamObserver<WatchUpdate> {

    @Override
    public void onNext(WatchUpdate update) {
        List<Event> events = update.getEvents();
        if (CollectionUtils.isNotEmpty(events)) {
            for (Event event : events) {
                if (event.getTypeValue() == Event.EventType.PUT_VALUE) {
                    KeyValue kv = event.getKv();
                    String key = kv.getKey().toStringUtf8();
                    String value = kv.getValue().toStringUtf8();
                    if (!key.equals(EdClient.getNamespace()) && !key.equals(EdClient.getNamespace() + "/")) {
                        key = key.substring(EdClient.getNamespace().length());
                        ConfigContext.getInstance().getConfigRefresh().chain(key, value);
                    }
                }
            }
        }
    }

    @Override
    public void onError(Throwable t) {
        log.error(t.getMessage());
    }

    @Override
    public void onCompleted() {
        // TODO Auto-generated method stub

    }

}
