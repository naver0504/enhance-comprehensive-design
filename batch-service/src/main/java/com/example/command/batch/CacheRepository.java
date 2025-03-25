package com.example.command.batch;

import java.util.Optional;
import java.util.function.Function;

public interface CacheRepository<K, V> {

    void save(K key, V value);

    Optional<V> findByKey(K key);

    V computeIfAbsent(K key, Function<K, V> function);
}
