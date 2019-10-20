package org.elasticsearch.plugin;

import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.inject.Binder;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.plugin.policy.sp.*;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;


public class PolicyPlugin extends Plugin implements ActionPlugin {


    @Override
    public List<RestHandler> getRestHandlers(Settings settings, RestController restController, ClusterSettings clusterSettings, IndexScopedSettings indexScopedSettings, SettingsFilter settingsFilter, IndexNameExpressionResolver indexNameExpressionResolver, Supplier<DiscoveryNodes> nodesInCluster) {
        return Arrays.asList(new RestHelloAction(settings,restController),
                new RestCreatePolicyAction(settings,restController),
                new RestNumAndSize(settings,restController),
                new RestDeletePolicyIndex(settings,restController),
                new RestPolicyAlias(settings,restController));
    }

    @Override
    public Collection<Module> createGuiceModules() {
        Module module=new Module() {
            @Override
            public void configure(Binder binder) {
                binder.bind(PolicyClusterStateListener.class).asEagerSingleton();
            }
        };
        return Collections.singletonList(module);
    }
}
