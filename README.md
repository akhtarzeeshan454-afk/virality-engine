# 🛡️ Virality Engine Guardrail System

A high-performance, stateless Spring Boot microservice designed to regulate automated bot interactions and prevent platform spam.

---

## 🚀 Key Features

### 🧵 Phase 1: Vertical Thread Depth Control
* **Limit:** Enforces a maximum thread depth of 5 levels.
* **Impact:** Eliminates the risk of infinite reply loops.

### 🛑 Phase 2: Horizontal Cap & Cooldowns
* **Horizontal Cap:** Strictly limits a post to 100 total bot replies.
* **Intra-Thread Cooldown:** Prevents a bot from replying to the same human for 10 minutes.

### 🔔 Phase 3: Smart Notification Batching
* **Throttling:** Checks for active user cooldowns.
* **Aggregation:** Batches rapid alerts into dedicated Redis lists.
* **Cron Sweeper:** Summarizes and flushes pending notifications.

---

## 🔒 Concurrency & Thread Safety

To survive the rigorous test of **200 concurrent requests** at the exact same millisecond, this system relies entirely on **Redis Atomic Operations**. 

### No Race Conditions
* Leverages Redis `INCR` (`opsForValue().increment()`).
* Operations are processed sequentially by single-threaded Redis.
* Stops at exactly 100 comments with zero margin of error.

### Guaranteed Data Integrity
* Performs an atomic rollback if limits are exceeded.
* Ensures database commits to **PostgreSQL** only occur after passing all Redis gatekeeper checks.

### Complete Statelessness
* Avoids local Java memory or `HashMap` structures.
* Allows seamless horizontal scaling across load balancers.

---

## ⚙️ Local Deployment

### 1. Spin up Infrastructure
Execute this command in the project root:
```bash
docker compose up -d
```

### 2. Launch the Application
Run `ViralityEngineApplication.java` via IntelliJ IDEA.

### 3. API Endpoint Testing
Access the endpoint directly via browser or Postman:
```text
http://localhost:8080/api/posts/1/reply?botId=1&humanId=10&currentDepth=1
```
