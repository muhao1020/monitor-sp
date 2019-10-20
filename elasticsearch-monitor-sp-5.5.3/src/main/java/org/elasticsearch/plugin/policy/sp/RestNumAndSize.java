package org.elasticsearch.plugin.policy.sp;

import com.carrotsearch.hppc.cursors.ObjectCursor;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.plugin.PolicyClusterStateListener;
import org.elasticsearch.plugin.policy.sp.bean.IndexInfo;
import org.elasticsearch.rest.*;
import org.elasticsearch.rest.action.RestActionListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RestNumAndSize extends BaseRestHandler {

    public RestNumAndSize(Settings settings, RestController restController) {
        super(settings);
        restController.registerHandler(RestRequest.Method.GET,"policy/infos",this);
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest restRequest, NodeClient nodeClient) throws IOException {
        IndicesStatsRequest indicesStatsRequest = new IndicesStatsRequest();
        ImmutableOpenMap<String, IndexMetaData> indices = PolicyClusterStateListener.clusterState.getMetaData().getIndices();
        String[] indexNames=new String[indices.size()];
        int i=0;
        for(ObjectCursor<String> s:indices.keys()){
            indexNames[i++]=s.value;
        }
        System.out.println("AAAA--- "+Arrays.toString(indexNames));
        indicesStatsRequest.indices(indexNames);
        final IndicesOptions strictExpandIndicesOptions = IndicesOptions.strictExpand();
        indicesStatsRequest.indicesOptions(strictExpandIndicesOptions);
        indicesStatsRequest.all();
        final Map<String, IndexInfo> indexInfoMap =new HashMap<>();
        nodeClient.admin().indices().stats(indicesStatsRequest, new RestActionListener<IndicesStatsResponse>(null) {
            @Override
            protected void processResponse(IndicesStatsResponse indicesStatsResponse) throws Exception {
                buildResult(indexInfoMap,indicesStatsResponse);
            }
        });
        System.out.println("hello number and size");
        System.out.println(indexInfoMap);
        return new RestChannelConsumer() {
            @Override
            public void accept(RestChannel restChannel) throws Exception {
                XContentBuilder builder = restChannel.newBuilder();
                builder.startObject();
                builder.field("message", "hello plugin");
                builder.endObject();
                BytesRestResponse response = new BytesRestResponse(RestStatus.OK, builder);
                restChannel.sendResponse(response);
            }
        };
    }

    private void buildResult(Map<String, IndexInfo> indexInfoMap, IndicesStatsResponse indicesStatsResponse) {
        Map<String, IndexStats> indices = indicesStatsResponse.getIndices();
        System.out.println("indices-- "+indices);
        MetaData indexMetaData = PolicyClusterStateListener.clusterState.metaData();
        System.out.println("indexMetaData-- "+indexMetaData);

        indices.forEach((index,indexStat)->{
            String indexName=index;
            long createTime=indexMetaData.getIndices().get(index).getCreationDate();
            long docNumber=indexStat.getPrimaries().getDocs().getCount();
            ByteSizeValue primaryByteSize=indexStat.getPrimaries().getStore().size();
            IndexInfo indexInfo = new IndexInfo(indexName, createTime, docNumber, primaryByteSize);
            System.out.println("indexInfo-- "+indexInfo);
            indexInfoMap.put(index, indexInfo);
        });
    }
}
