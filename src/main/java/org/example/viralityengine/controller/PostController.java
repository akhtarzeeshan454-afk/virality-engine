package org.example.viralityengine.controller;

import org.example.viralityengine.model.Post; // Added this import
import org.example.viralityengine.repository.PostRepository; // Added this import
import org.example.viralityengine.service.GuardrailService;
import org.example.viralityengine.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private GuardrailService guardrailService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PostRepository postRepository; // 1. Added database repository

    @GetMapping("/{postId}/reply")
    public ResponseEntity<String> botReply(
            @PathVariable Long postId,
            @RequestParam Long botId,
            @RequestParam Long humanId,
            @RequestParam int currentDepth) {

        // Phase 1: Check thread depth
        if (currentDepth >= 5) {
            return ResponseEntity.badRequest().body("Vertical cap reached: Thread depth cannot exceed 5.");
        }

        // Phase 2: Check bot count
        if (!guardrailService.checkHorizontalCap(postId)) {
            return ResponseEntity.badRequest().body("Horizontal cap reached: Max 100 bot replies allowed.");
        }

        // Phase 2: Check cooldown
        if (!guardrailService.checkCooldown(botId, humanId)) {
            return ResponseEntity.badRequest().body("Cooldown active: This bot cannot reply to this human yet.");
        }

        // 2. Data Integrity: ONLY save to Postgres if the above Redis checks passed
        Post reply = new Post();
        reply.setAuthorId(botId);
        reply.setContent("Bot reply at depth " + currentDepth);
        postRepository.save(reply); // Committing to DB

        // Phase 3: Add notification
        notificationService.addNotification(humanId, "Bot " + botId + " replied to your post.");

        return ResponseEntity.ok("Bot reply successful at depth " + currentDepth + "!");
    }
}
