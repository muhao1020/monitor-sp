package org.elasticsearch.plugin;

import com.carrotsearch.hppc.cursors.ObjectCursor;
import org.elasticsearch.cluster.ClusterChangedEvent;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.ClusterStateListener;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.inject.Inject;

import java.util.Arrays;

public class PolicyClusterStateListener implements ClusterStateListener {
    public static ClusterState clusterState;

    @Inject
    public PolicyClusterStateListener(ClusterService clusterService) {
        clusterService.addListener(this);
    }

    @Override
    public void clusterChanged(ClusterChangedEvent clusterChangedEvent) {
        clusterState=clusterChangedEvent.state();
        System.out.println("AAAA:\n"+clusterState.getMetaData());
        ImmutableOpenMap<String, IndexMetaData> indices = clusterState.getMetaData().getIndices();
        String[] indexNames=new String[indices.size()];
        int i=0;
        for(ObjectCursor<String> s:indices.keys()){
            indexNames[i++]=s.value;
        }
        System.out.println(Arrays.toString(indexNames));
    }
}
