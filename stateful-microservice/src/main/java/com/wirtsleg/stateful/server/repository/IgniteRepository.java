package com.wirtsleg.stateful.server.repository;

import java.io.Serializable;
import java.util.Map;
import javax.cache.expiry.ExpiryPolicy;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * New version of Spring Data has different signatures from
 * {@link org.apache.ignite.springdata22.repository.IgniteRepository}, so we have to create this interface
 * until it is fixed.
 */
@NoRepositoryBean
public interface IgniteRepository<V, K extends Serializable> extends CrudRepository<V, K> {
    Ignite ignite();

    IgniteCache<K, V> cache();

    <S extends V> S save(K key, S entity);

    <S extends V> Iterable<S> save(Map<K, S> entities);

    <S extends V> S save(K key, S entity, @Nullable ExpiryPolicy expiryPlc);

    <S extends V> Iterable<S> save(Map<K, S> entities, @Nullable ExpiryPolicy expiryPlc);

    V deleteAndGetById(K id);
}
