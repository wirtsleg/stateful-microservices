package com.wirtsleg.stateful.client.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@LoadBalancerClient(name = WebClientConfig.CLIENT_NAME, configuration = IgniteLoadBalancerConfiguration.class)
public class WebClientConfig {
    public static final String CLIENT_NAME = "client";

    @Bean
    @LoadBalanced
    public WebClient.Builder usersClientBuilder() {
        return WebClient.builder()
            .defaultHeader("affinity-cache-name", "UserCache");
    }
}
