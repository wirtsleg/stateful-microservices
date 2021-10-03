package com.wirtsleg.stateful.client.web;

import com.wirtsleg.stateful.client.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TestController {
    private final WebClient.Builder usersClientBuilder;

    @GetMapping("/users/{userId}")
    public Mono<User> getUser(@PathVariable String userId) {
        return usersClientBuilder.build().get().uri("http://client/api/v1/users/" + userId)
            .header("affinity-key", userId)
            .retrieve()
            .bodyToMono(User.class);
    }
}
