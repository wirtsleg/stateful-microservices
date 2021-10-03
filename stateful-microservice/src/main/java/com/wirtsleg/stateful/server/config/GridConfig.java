package com.wirtsleg.stateful.server.config;

import java.util.Arrays;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GridConfig {
    public static final String IGNITE_NAME = "ignite";

    @Value("${ignite.consistentId}")
    private String consistentId;

    @Value("${server.port}")
    private int port;

    @Bean(IGNITE_NAME)
    public IgniteEx ignite() {
        IgniteConfiguration cfg = new IgniteConfiguration()
            .setConsistentId(consistentId)
            .setUserAttributes(U.map(
                "load_balancing_host", "localhost",
                "load_balancing_port", port
            ))
            .setDiscoverySpi(new TcpDiscoverySpi()
                .setIpFinder(new TcpDiscoveryMulticastIpFinder()
                .setAddresses(Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"))));

        try {
            return (IgniteEx)Ignition.start(cfg);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
