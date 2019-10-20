package org.elasticsearch.plugin.policy.sp;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.*;

import java.io.IOException;

public class RestPolicyAlias extends BaseRestHandler {

    public RestPolicyAlias(Settings settings, RestController restController) {
        super(settings);
        restController.registerHandler(RestRequest.Method.POST,"policy/_aliases",this);
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest restRequest, NodeClient nodeClient) throws IOException {
        IndicesAliasesRequest indicesAliasesRequest = new IndicesAliasesRequest();
        indicesAliasesRequest.masterNodeTimeout(restRequest.paramAsTime("master_timeout", indicesAliasesRequest.masterNodeTimeout()));
        indicesAliasesRequest.timeout(restRequest.paramAsTime("timeout", indicesAliasesRequest.timeout()));

        IndicesAliasesRequest.AliasActions addNew1=IndicesAliasesRequest.AliasActions.add();
        addNew1.alias("alias1a").index("logsa");
        IndicesAliasesRequest.AliasActions addNew2=IndicesAliasesRequest.AliasActions.add();
        addNew2.alias("alias2a").index("logsaa");
        IndicesAliasesRequest.AliasActions remove = IndicesAliasesRequest.AliasActions.remove();
        remove.index("logsa").alias("alias1");
        indicesAliasesRequest.addAliasAction(addNew1);
        indicesAliasesRequest.addAliasAction(addNew2);
        indicesAliasesRequest.addAliasAction(remove);

        nodeClient.admin().indices().aliases(indicesAliasesRequest, new ActionListener() {

            @Override
            public void onResponse(Object o) {
                System.out.println("alise succeed");
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
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
}
