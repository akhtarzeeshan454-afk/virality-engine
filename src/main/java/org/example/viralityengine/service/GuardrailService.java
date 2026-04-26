package org.example.viralityengine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class GuardrailService {

    @Autowired
    private StringRedisTemplate redis;

    // Phase 2: Cap total bot replies to 100 per post
    public boolean checkHorizontalCap(Long postId) {
        String key = "post:" + postId + ":bot_count";
        Long count = redis.opsForValue().increment(key);

        if (count != null && count > 100) {
            redis.opsForValue().decrement(key); // Rollback if limit exceeded
            return false;
        }
        return true;
    }

    // Phase 2: Cooldown - Bot cannot reply to the same human for 10 minutes
    public boolean checkCooldown(Long botId, Long humanId) {
        String key = "cooldown:bot_" + botId + ":human_" + humanId;

        // If key exists, cooldown is still active
        Boolean isLocked = redis.hasKey(key);
        if (Boolean.TRUE.equals(isLocked)) {
            return false;
        }

        // Set cooldown lock for 10 minutes
        redis.opsForValue().set(key, "active", 10, TimeUnit.MINUTES);
        return true;
    }
}
