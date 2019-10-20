package org.elasticsearch.plugin.policy.sp;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.*;

import java.io.IOException;

public class RestHelloAction extends BaseRestHandler {

    public RestHelloAction(Settings settings, RestController restController) {
        super(settings);
        restController.registerHandler(RestRequest.Method.GET, "/hello/muhao", this);
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest restRequest, NodeClient nodeClient) throws IOException {
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
