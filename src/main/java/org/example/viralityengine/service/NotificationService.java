package org.example.viralityengine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class NotificationService {

    @Autowired
    private StringRedisTemplate redis;

    public void addNotification(Long userId, String message) {
        String cooldownKey = "user:" + userId + ":cooldown";
        String listKey = "user:" + userId + ":pending_notifs";

        Boolean hasCooldown = redis.hasKey(cooldownKey);

        if (Boolean.TRUE.equals(hasCooldown)) {
            redis.opsForList().rightPush(listKey, message);
        } else {
            System.out.println("Push Notification Sent to User");
            // Set cooldown for 10 SECONDS instead of 15 minutes
            redis.opsForValue().set(cooldownKey, "active", 10, TimeUnit.SECONDS);
        }
    }

    // Sweeper runs every 10 SECONDS instead of 5 minutes
    @Scheduled(fixedRate = 10000)
    public void sweepNotifications() {
        Set<String> keys = redis.keys("user:*:pending_notifs");
        if (keys == null) return;

        for (String key : keys) {
            String[] parts = key.split(":");
            String userId = parts[1];

            Long count = redis.opsForList().size(key);

            if (count != null && count > 0) {
                String firstMessage = redis.opsForList().index(key, 0);
                String botName = firstMessage != null ? firstMessage.split(" ")[0] : "Bot";

                System.out.println("Summarized Push Notification: " + botName
                        + " and [" + (count - 1) + "] others interacted with your posts.");

                redis.delete(key);
            }
        }
    }
}
