package com.example.query.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

@Configuration
@EnableCaching(order = LOWEST_PRECEDENCE - 1)
public class CacheConfiguration {

    @Bean
    public Caffeine caffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .scheduler(Scheduler.systemScheduler());
    }


    @Bean
    public CacheManager cacheManager(Caffeine caffeineConfig) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeineConfig);
        return caffeineCacheManager;
    }
}
