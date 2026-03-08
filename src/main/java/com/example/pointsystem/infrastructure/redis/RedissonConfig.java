package com.example.pointsystem.infrastructure.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Redisson 클라이언트 설정을 제공합니다.
 */
@Configuration
public class RedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();

        String address = "redis://" + redisProperties.getHost() + ":" + redisProperties.getPort();
        SingleServerConfig singleServer = config.useSingleServer()
                .setAddress(address)
                .setDatabase(redisProperties.getDatabase());

        Duration connectTimeout = redisProperties.getConnectTimeout();
        if (connectTimeout != null) {
            singleServer.setConnectTimeout((int) connectTimeout.toMillis());
        }

        if (redisProperties.getPassword() != null && !redisProperties.getPassword().isBlank()) {
            singleServer.setPassword(redisProperties.getPassword());
        }

        return Redisson.create(config);
    }
}
