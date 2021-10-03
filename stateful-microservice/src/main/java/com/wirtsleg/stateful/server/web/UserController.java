package com.wirtsleg.stateful.server.web;

import java.util.NoSuchElementException;
import com.wirtsleg.stateful.server.dto.User;
import com.wirtsleg.stateful.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepo;

    @GetMapping("/users/{userId}")
    public User getUser(@PathVariable String userId) {
        return userRepo.findById(userId)
            .orElseThrow(NoSuchElementException::new);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() {
        userRepo.save("neo", new User("neo", "neo@matrix.com", "Mr. Anderson"));
        userRepo.save("monkeydluffy", new User("monkeydluffy", "monkeydluffy@onepiece.com", "Monkey D. Luffy"));
        userRepo.save("valarmorgullis", new User("vallarmorgulis", "vallarmorgulis@gameofthrones.com", "Valar Morgullis"));
    }
}
