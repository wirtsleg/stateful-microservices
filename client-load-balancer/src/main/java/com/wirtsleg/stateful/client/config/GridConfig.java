package com.wirtsleg.stateful.client.config;

import java.util.Arrays;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class GridConfig {

    @Bean("ignite")
    public IgniteEx ignite() {
        IgniteConfiguration cfg = new IgniteConfiguration()
            .setClientMode(true)
            .setDiscoverySpi(new TcpDiscoverySpi()
                .setIpFinder(new TcpDiscoveryMulticastIpFinder()
                .setAddresses(Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"))));

        return (IgniteEx)Ignition.start(cfg);
    }
}
