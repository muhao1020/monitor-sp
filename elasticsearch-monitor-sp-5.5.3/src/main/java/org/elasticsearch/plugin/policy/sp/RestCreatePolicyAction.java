package org.elasticsearch.plugin.policy.sp;

import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.support.ActiveShardCount;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.plugin.policy.sp.util.Base64Util;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.AcknowledgedRestListener;
import org.elasticsearch.rest.action.admin.indices.RestCreateIndexAction;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Map;

public class RestCreatePolicyAction extends BaseRestHandler {
//    private RestCreateIndexAction indexAction;

    public RestCreatePolicyAction(Settings settings, RestController restController) {
        super(settings);
//        RestController fakeController = new RestController(settings, null, null, null, null);
//        indexAction = new RestCreateIndexAction(settings, fakeController);
        restController.registerHandler(RestRequest.Method.PUT, "policy/{index}", this);
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
        Map<String, String> params = request.params();
        String orginIndex=params.get("index");
        String index= assemblyIndexNameByParam(params);
        System.out.println("old index : " + orginIndex);
        System.out.println("new index : " + index);
        params.put("index", index);
        params.remove("unit");
        params.remove("number");

        CreateIndexRequest createIndexRequest = new CreateIndexRequest(request.param("index"));
        if (request.hasContent()) {
            createIndexRequest.source(request.content(), request.getXContentType());
        }
        createIndexRequest.alias(new Alias(orginIndex)).alias(new Alias(orginIndex+"_write"));
        createIndexRequest.updateAllTypes(request.paramAsBoolean("update_all_types", false));
        createIndexRequest.timeout(request.paramAsTime("timeout", createIndexRequest.timeout()));
        createIndexRequest.masterNodeTimeout(request.paramAsTime("master_timeout", createIndexRequest.masterNodeTimeout()));
        createIndexRequest.waitForActiveShards(ActiveShardCount.parseString(request.param("wait_for_active_shards")));
        return channel -> client.admin().indices().create(createIndexRequest, new AcknowledgedRestListener<CreateIndexResponse>(channel) {
            @Override
            public void addCustomFields(XContentBuilder builder, CreateIndexResponse response) throws IOException {
                response.addCustomFields(builder);
            }
        });
    }

    private String assemblyIndexNameByParam(Map<String, String> params) throws UnsupportedEncodingException {
        String result = null;
        if (!params.containsKey("index") || !params.containsKey("unit") || !params.containsKey("number")) {
            throw new IllegalArgumentException("params must contains unit and number");
        }
        String index = params.get("index");
        String unit = params.get("unit");
        String number = params.get("number");
        // index-base64-nowState
        return index+"-"+saveStrategyBase64(index,unit,number)+"-"+createCurrentState(unit);
    }

    // 将保存信息写入 index name中
    private String saveStrategyBase64(String index, String unit, String number) throws UnsupportedEncodingException {
        // unit :
        // time : Yt(yz) 、Mt(mz) 、dt 、Ht 、mt 、st
        // size : gb 、mb 、kb 、bb
        // doc  : gd 、 md 、kd 、dd
        // index 的 第一个char 和最后一个 char + unit + number   : 构成 base64
        // 此函数返回的 base64 string
        String value = String.valueOf(new char[]{index.charAt(0), index.charAt(index.length() - 1)}) + unit + number;
        // base64 加密由于大写限制，目前不做。理想情况是根据这种方式唯一标识 policy 。
//        return Base64Util.encodeStr(value);
        return value;
    }

    // 根据 unit 获取相应的状态 ，只是对 time 有意义,对于其他的返回 1 ，表示是第一个index
    private String createCurrentState(String unit) {
        if (unit.charAt(1) != 't' && unit.charAt(1) != 'z' ) {
            return "1";
        }else if(unit.charAt(1) == 'z'){
            unit=unit.toUpperCase();
        }
        String format="YYYYMMddHHmmss";
        String substring = format.substring(0, 1+format.lastIndexOf(unit.charAt(0)));
        SimpleDateFormat sdf = new SimpleDateFormat(substring);
        return sdf.format(System.currentTimeMillis());
    }


}
