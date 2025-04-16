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

# 🧠 Traditional Java vs Reactive Java – A Practical Comparison

This document compares a real-world REST API example written in **Reactive Java (Spring WebFlux + Project Reactor)** with how it would typically look using **Traditional Java (Spring MVC)**.

---

## 📌 Use Case

We want to build a REST API to:
- Fetch paginated stock symbols from MongoDB
- Support optional keyword search
- Return total count and current page results

---

## ⚙️ Traditional Java (Spring MVC)

### ✅ Code Example

```java
@GetMapping("/symbols")
public PageResponse<Symbol> getSymbols(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String q
) {
    Query query = buildQuerySearchWithTerm(q);
    query.with(PageRequest.of(page, size));
    List<Symbol> data = mongoTemplate.find(query, Symbol.class);

    long total = mongoTemplate.count(buildQuerySearchWithTerm(q), Symbol.class);

    return PageResponse.builder()
            .total(total)
            .data(data)
            .page(page)
            .size(size)
            .build();
}
```

### 🚦 Characteristics

| Feature | Description |
|--------|-------------|
| **Blocking** | Each MongoDB call blocks the thread until the result is returned. |
| **Thread per request** | Each incoming request is handled by a thread, which waits during I/O. |
| **Simple to debug** | Easier stack traces and linear flow. |
| **Good for low concurrency** | Performs well under light or moderate traffic. |
| **Resource intensive** | Threads are blocked during DB calls, which can limit scalability. |

---

## ⚡ Reactive Java (Spring WebFlux)

### ✅ Code Example

```java
@GetMapping("/symbols")
public Mono<PageResponse<Object>> getSymbols(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String q
) {
    Flux<Symbol> listSymbols = symbolService.getSymbols(q, page, size);
    Mono<Long> totalSymbols = symbolService.countSymbols(q);

    return listSymbols.collectList()
            .zipWith(totalSymbols, (symbols, total) -> PageResponse.builder()
                    .total(total)
                    .data(new ArrayList<>(symbols))
                    .page(page)
                    .size(size)
                    .build());
}
```

### 🚦 Characteristics

| Feature | Description |
|--------|-------------|
| **Non-blocking** | I/O operations are async, freeing up threads for other tasks. |
| **Backpressure support** | Works well with streams of data, can control load with backpressure. |
| **Scalable** | Ideal for high-concurrency apps (many connections with slow clients). |
| **Harder to debug** | Flow is not linear; debugging can be tricky. |
| **Cold by default** | Streams won’t start until subscribed. |
| **Lazy Evaluation** | Operations only run when the final subscriber requests data. |

---

## 🧠 Technical Differences Summary

| Aspect | Traditional Java (Spring MVC) | Reactive Java (WebFlux + Reactor) |
|--------|-------------------------------|-----------------------------------|
| **Execution Model** | Thread-per-request | Event loop / Non-blocking |
| **Return Type** | POJOs / Collections | `Mono<T>` / `Flux<T>` |
| **Data Flow** | Eager (Immediate) | Lazy (Only on subscription) |
| **Concurrency Handling** | Blocking I/O | Non-blocking I/O |
| **Thread Usage** | More threads required | Fewer threads, higher throughput |
| **Scalability** | Limited | High (ideal for thousands of concurrent clients) |
| **Error Handling** | `try-catch` | `.onErrorXxx()` operators |
| **Debuggability** | Easier | Requires understanding of Reactive streams |
| **Memory Consumption** | Higher per thread | Lower with async I/O |
| **Learning Curve** | Beginner friendly | Steeper (needs understanding of reactive concepts) |

---

## ✅ When to Use What?

| Use Case | Recommended Approach |
|----------|----------------------|
| Low traffic API with CPU-bound work | Traditional Java (MVC) |
| Simple CRUD apps | Traditional Java (MVC) |
| High-concurrency APIs (e.g. chat, streaming) | Reactive Java (WebFlux) |
| Backend-for-frontend apps with async data | Reactive |
| Microservices chaining multiple APIs | Reactive (non-blocking avoids thread starvation) |
| You need easy debugging & fast delivery | Traditional Java |

---

## 🛠 Example Comparison of Return Types

### Traditional
```java
public List<Symbol> getSymbols(...) {
    return mongoTemplate.find(query, Symbol.class);
}
```

### Reactive
```java
public Flux<Symbol> getSymbols(...) {
    return reactiveMongoTemplate.find(query, Symbol.class);
}
```
