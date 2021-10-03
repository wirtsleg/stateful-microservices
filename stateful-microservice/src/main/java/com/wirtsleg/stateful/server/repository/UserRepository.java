package com.wirtsleg.stateful.server.repository;

import com.wirtsleg.stateful.server.dto.User;
import org.apache.ignite.springdata22.repository.config.RepositoryConfig;

import static com.wirtsleg.stateful.server.config.GridConfig.IGNITE_NAME;

@RepositoryConfig(cacheName = "UserCache", igniteInstance = IGNITE_NAME, autoCreateCache = true)
public interface UserRepository extends IgniteRepository<User, String> {
}
