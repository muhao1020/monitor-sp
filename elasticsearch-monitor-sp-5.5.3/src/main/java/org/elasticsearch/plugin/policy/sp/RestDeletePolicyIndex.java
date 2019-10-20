package org.elasticsearch.plugin.policy.sp;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.AcknowledgedRestListener;

import java.io.IOException;

public class RestDeletePolicyIndex extends BaseRestHandler {
    public RestDeletePolicyIndex(Settings settings, RestController restController) {
        super(settings);
        restController.registerHandler(RestRequest.Method.DELETE, "policy/{index}", this);
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest restRequest, NodeClient nodeClient) throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(Strings.splitStringByCommaToArray(restRequest.param("index")));
        deleteIndexRequest.timeout(restRequest.paramAsTime("timeout", deleteIndexRequest.timeout()));
        deleteIndexRequest.masterNodeTimeout(restRequest.paramAsTime("master_timeout", deleteIndexRequest.masterNodeTimeout()));
        deleteIndexRequest.indicesOptions(IndicesOptions.fromRequest(restRequest, deleteIndexRequest.indicesOptions()));
        return channel -> nodeClient.admin().indices().delete(deleteIndexRequest, new AcknowledgedRestListener<>(channel));
    }
}
