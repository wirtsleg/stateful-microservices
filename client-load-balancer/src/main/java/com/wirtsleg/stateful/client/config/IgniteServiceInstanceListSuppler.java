package com.wirtsleg.stateful.client.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.events.DiscoveryEvent;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.managers.discovery.DiscoCache;
import org.apache.ignite.internal.processors.cache.GatewayProtectedCacheProxy;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;

import static com.wirtsleg.stateful.client.config.IgniteLoadBalancerConfiguration.SERVICE_ID;
import static java.util.Collections.singletonList;
import static org.apache.ignite.events.EventType.EVT_NODE_FAILED;
import static org.apache.ignite.events.EventType.EVT_NODE_JOINED;
import static org.apache.ignite.events.EventType.EVT_NODE_LEFT;
import static org.apache.ignite.events.EventType.EVT_NODE_SEGMENTED;

public class IgniteServiceInstanceListSuppler implements ServiceInstanceListSupplier {
    private static final int[] EVTS_DISCOVERY = new int[] {
        EVT_NODE_JOINED, EVT_NODE_LEFT, EVT_NODE_FAILED, EVT_NODE_SEGMENTED
    };

    private final IgniteEx ignite;
    private final Map<String/*Cache name*/, Affinity<Object>> affinities = new ConcurrentHashMap<>();

    private volatile List<ServiceInstance> instances;

    public IgniteServiceInstanceListSuppler(IgniteEx ignite) {
        this.ignite = ignite;

        ignite.context().event().addDiscoveryEventListener(this::topologyChanged, EVTS_DISCOVERY);
        updateInstances(ignite.cluster().nodes());
    }

    private void topologyChanged(DiscoveryEvent event, DiscoCache cache) {
        updateInstances(event.topologyNodes());

        affinities.clear();
    }

    private void updateInstances(Collection<ClusterNode> nodes) {
        instances = nodes.stream()
            .filter(node -> !node.isClient())
            .map(this::toServiceInstance)
            .collect(Collectors.toList());
    }

    private ServiceInstance toServiceInstance(ClusterNode node) {
        return new DefaultServiceInstance(
            node.consistentId().toString(),
            SERVICE_ID,
            node.attribute("load_balancing_host"),
            node.attribute("load_balancing_port"),
            false
        );
    }

    @Override
    public String getServiceId() {
        return ignite.localNode().consistentId().toString();
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return Flux.just(instances);
    }

    @Override
    public Flux<List<ServiceInstance>> get(Request req) {
        if (req.getContext() instanceof RequestDataContext) {
            HttpHeaders headers = ((RequestDataContext)req.getContext()).getClientRequest().getHeaders();

            String cacheName = headers.getFirst("affinity-cache-name");
            String affinityKey = headers.getFirst("affinity-key");

            Affinity<Object> affinity = affinities.computeIfAbsent(
                cacheName, k -> ((GatewayProtectedCacheProxy)ignite.cache(cacheName)).context().cache().affinity()
            );

            ClusterNode node = affinity.mapKeyToNode(affinityKey);

            if (node != null)
                return Flux.just(singletonList(toServiceInstance(node)));
        }

        return get();
    }
}
