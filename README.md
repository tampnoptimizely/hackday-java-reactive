# 📘 Reactive Java Learning Guide

## 🎯 Goal

Learn how to write **non-blocking**, **asynchronous**, and **event-driven** applications using **Reactive Java** (Project Reactor, Spring WebFlux).

---

## 📍 PART 1: Roadmap to Learn Reactive Java

### 🔰 Step 1: Understand the "Why"

| Traditional Java | Reactive Java |
|------------------|----------------|
| Blocking, imperative | Non-blocking, functional |
| Thread-per-request model | Efficient event-loop |
| Wastes resources on I/O wait | Scales better under load |

Use cases: high concurrency, real-time systems, chat apps, stock dashboards, streaming APIs, etc.

---

### 🧭 Step 2: Core Concepts

| Concept | Description |
|--------|-------------|
| **Publisher / Subscriber** | Core reactive pattern: publisher emits data, subscriber consumes it. |
| **Mono / Flux** | `Mono<T>` = 0..1 values, `Flux<T>` = 0..∞ values |
| **Backpressure** | Handles fast producers vs slow consumers |
| **Operators** | Transform data: `map`, `flatMap`, `filter`, etc. |
| **Schedulers** | Control execution threads (event loop vs blocking) |

---

### 🚀 Step 3: Learning Stages

#### 🧩 Beginner
- Learn basic Project Reactor (`Mono`, `Flux`)
- Use operators (`map`, `flatMap`, `filter`)
- Simulate data with `Flux.interval`

#### 🔄 Intermediate
- Understand thread switching (`subscribeOn`, `publishOn`)
- Combine streams (`merge`, `zip`, `concat`)
- Add backpressure handling

#### 💡 Advanced
- Build APIs with **Spring WebFlux**
- Use **WebClient** for non-blocking HTTP
- Connect with **R2DBC** (Reactive DB access)

---

## 📍 PART 2: Traditional vs Reactive Java

### 📌 Blocking I/O Example (Legacy Java)

```java
public String getUser() {
    // Blocking HTTP or DB call
    String user = userService.getUserSync();
    return user;
}
```

### ⚡ Non-blocking I/O Example (Reactive Java)

```java
public Mono<String> getUser() {
    // Returns a Mono that will emit the user later
    return userService.getUserAsync();
}
```

---

### 📌 REST Call Comparison

**Legacy (RestTemplate, Blocking):**

```java
String result = restTemplate.getForObject("http://api", String.class);
System.out.println("Got response: " + result);
```

**Reactive (WebClient, Non-blocking):**

```java
WebClient client = WebClient.create();
client.get()
      .uri("http://api")
      .retrieve()
      .bodyToMono(String.class)
      .subscribe(response -> System.out.println("Got response: " + response));
```

---

## 📚 Learning Resources

| Level | Resource |
|-------|----------|
| Beginner | [Project Reactor Docs](https://projectreactor.io/docs/core/release/reference/) |
| Spring-based | [Spring WebFlux Guide](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html) |
| Book | *Hands-On Reactive Programming in Spring 5* |
| Video | YouTube: *Spring WebFlux Crash Course*, *Project Reactor Intro* |

---

## 💡 Hackday Project Ideas

### 🔹 Reactive Stock Tracker
- Simulate real-time stock price updates with `Flux.interval`
- Use `WebClient` to call an API
- Use `map`, `filter`, `flatMap` to transform streams
- (Bonus) Expose as a REST API via Spring WebFlux

### 🔹 Reactive Chat Simulation
- Each user is a stream emitting messages
- Merge streams into chatroom output
- Add filtering, transformation, delay

---

## ✅ Next Steps

- [ ] Install Java 17+
- [ ] Create basic Maven/Gradle project
- [ ] Practice with `Mono.just(...)` and `Flux.range(...)`
- [ ] Build a reactive API using Spring WebFlux

---