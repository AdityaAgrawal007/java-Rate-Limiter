package com.cerberus.rateLimiter.store;

import com.cerberus.rateLimiter.core.RateLimitResult;
import com.cerberus.rateLimiter.core.StateStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class RedisStateStore implements StateStore {
    private final RedisTemplate<String, Object> redisTemplate;
    private final DefaultRedisScript<List> rateLimitScript;

    public RedisStateStore(RedisTemplate<String, Object> redisTemplate) throws IOException {
        this.redisTemplate = redisTemplate;

        // Load Lua script from classpath (relative to resources folder)
        ClassPathResource resource = new ClassPathResource("rate_limit.lua");
        String lua = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        this.rateLimitScript = new DefaultRedisScript<>();
        this.rateLimitScript.setScriptText(lua);
        this.rateLimitScript.setResultType(List.class);
    }

    @Override
    public RateLimitResult tryConsume(String clientKey, long tokenLimit, Duration timeWindow) {
        List<Long> result = redisTemplate.execute(
                rateLimitScript,
                Collections.singletonList(clientKey),
                String.valueOf(tokenLimit),
                String.valueOf(timeWindow.getSeconds())
        );

        return new RateLimitResult(
                result.get(0) == 1,
                result.get(1),
                result.get(2)
        );
    }
}