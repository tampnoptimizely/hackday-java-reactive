# 📘 Reactive Java Learning Guide
# 🔄 Understanding Reactive Java: What, Why, When, and How

## 🔍 What is Reactive Java?

**Reactive Java** is a programming paradigm focused on building asynchronous, non-blocking, and event-driven systems that can handle a large number of concurrent data streams with minimal resources. It follows the principles of the **Reactive Manifesto**—**responsive**, **resilient**, **elastic**, and **message-driven**.

At its core, Reactive Java is built on the **Reactive Streams** specification, which defines a standard for asynchronous stream processing with **non-blocking backpressure**. Key libraries and frameworks that implement these principles include:

- **Project Reactor** (used by Spring WebFlux)
- **RxJava**
- **Akka Streams**
- **Vert.x**

Reactive Java represents a shift from traditional imperative and blocking code to a **declarative, event-driven, and functional** approach.

---

## ❓ Why Do We Need Reactive Java?

### 1. **Scalability**
Traditional thread-per-request models struggle under high load due to limited thread and memory resources. Reactive Java allows handling thousands of concurrent requests with a small thread pool using event loops.

### 2. **Responsiveness**
Reactive systems are designed to respond promptly even under high throughput by avoiding blocking operations. This leads to better user experiences and lower latencies.

### 3. **Efficient Resource Utilization**
Reactive code allows you to perform I/O-bound operations without blocking threads—ideal for cloud-native and microservice architectures where resources are limited.

### 4. **Built-in Backpressure**
Reactive Streams provides flow control mechanisms to avoid overloading consumers, ensuring data is processed at a sustainable rate.

---

## 🕒 When Should You Use Reactive Java?

| Scenario | Reactive Java Fit? |
|----------|--------------------|
| High-volume, concurrent API requests (e.g., public-facing APIs) | ✅ Excellent |
| Streaming data (Kafka, WebSockets, SSE) | ✅ Excellent |
| Real-time systems (e.g., dashboards, chat apps) | ✅ Excellent |
| CPU-bound, computational-heavy tasks | ❌ Traditional Java preferred |
| Integrating with legacy blocking APIs | ❌ Best with traditional approach or adapters |
| Small/simple internal apps | ❌ May add unnecessary complexity |

---

## 🛠️ How to Use Reactive Java

### 1. **Choose a Reactive Framework**
- **Spring WebFlux (Project Reactor)** – best for Spring-based apps
- **RxJava** – popular in Android and standalone reactive apps
- **Vert.x / Akka Streams** – suitable for low-latency, distributed systems

---

### 2. **Use Reactive Types**
- `Mono<T>` – Emits 0 or 1 item (like `Optional`)
- `Flux<T>` – Emits 0…N items (like `Stream`)

Example using **Spring WebFlux**:

```java
@GetMapping("/users")
public Flux<User> getAllUsers() {
    return userRepository.findAll(); // Non-blocking reactive repository
}
```

---

### 3. **Reactive Database Access**
Use **R2DBC** for reactive relational database access:

```java
@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Flux<User> findByAgeGreaterThan(int age);
}
```

For NoSQL databases like MongoDB, use `ReactiveMongoRepository`.

---

### 4. **Async Composition with Operators**

Reactive Java provides a rich set of operators for transforming and composing async data:

```java
userService.getUserById(id)
    .flatMap(user -> orderService.getOrdersByUser(user))
    .map(orders -> buildResponse(user, orders))
    .onErrorResume(error -> Mono.just(defaultResponse()))
    .subscribe(response -> send(response));
```

---

### 5. **Threading and Scheduling**

Use `.subscribeOn()` and `.publishOn()` to control execution context:

```java
someReactiveOperation()
    .subscribeOn(Schedulers.boundedElastic()) // for blocking calls
    .publishOn(Schedulers.parallel())
    .subscribe();
```

---

### 6. **Backpressure Handling**

Backpressure is automatically managed in Project Reactor. But you can also control it:

```java
Flux.range(1, 1000)
    .onBackpressureDrop()
    .subscribe(System.out::println);
```

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

### ✅ **Traditional Java vs Reactive Java – A Technical Comparison**

| Aspect | Traditional Java | Reactive Java |
|--------|------------------|---------------|
| **Programming Model** | Imperative / Blocking | Declarative / Non-blocking |
| **Thread Management** | One thread per task/request (e.g., in Servlet model) | Event-loop / Callback-based, few threads handle many requests |
| **Concurrency** | Relies on `Thread`, `ExecutorService`, and synchronization primitives like `synchronized`, `Lock`, etc. | Uses reactive streams, schedulers, and asynchronous processing without explicit thread management |
| **I/O Model** | Blocking I/O (e.g., `InputStream`, JDBC) | Non-blocking I/O (e.g., `WebClient`, `R2DBC`) |
| **Libraries/Frameworks** | Spring MVC, Java EE, Servlets, JAX-RS | Project Reactor, RxJava, Spring WebFlux, Vert.x |
| **Error Handling** | Try-catch blocks around sequential code | Operators like `onErrorResume`, `onErrorContinue` to handle async errors |
| **Scalability** | Limited by thread pool size and memory | Highly scalable due to event-driven, async nature |
| **Backpressure** | Manual throttling / buffering | Built-in in reactive streams specification (Reactive Streams API) |
| **Learning Curve** | Easier for most developers; familiar flow | Steeper curve; requires functional & async mindset |
| **Debugging** | Straightforward with IDEs and stack traces | Can be tricky; async stack traces can be harder to follow |
| **Use Cases** | Ideal for CPU-bound, blocking tasks, legacy systems | Best for high-throughput, IO-bound, concurrent applications (e.g., microservices, streaming, APIs) |

---
## 🔁 `Flux<T>` vs `List<T>` – Key Differences in DB Access
### 🔍 Example
#### Traditional Java (Blocking)
```java
public List<Symbol> getSymbols(String term) {
    Query query = buildQuery(term);
    return mongoTemplate.find(query, Symbol.class); // Blocking
}
```

- The thread calling this method is **blocked** until the full result is fetched from MongoDB.
- Good for simple use cases but doesn't scale well under high load.

---

#### Reactive Java (Non-blocking)
```java
public Flux<Symbol> getSymbols(String term) {
    Query query = buildQuery(term);
    return reactiveMongoTemplate.find(query, Symbol.class); // Non-blocking
}
```

- Returns a `Flux<Symbol>`, which emits symbols **one at a time** (or in chunks) as they become available.
- Does **not block** the thread. It subscribes only when needed.
- More scalable in I/O-heavy applications.

---

| Aspect | `List<T>` (Traditional) | `Flux<T>` (Reactive) |
|--------|--------------------------|------------------------|
| **Type** | Eager collection (all data loaded immediately) | Asynchronous stream of items (loaded reactively) |
| **Execution** | Blocking – the thread waits for DB to respond | Non-blocking – the thread doesn’t wait, continues execution |
| **Returned by** | `JpaRepository`, `MongoTemplate`, `JdbcTemplate`, etc. | `ReactiveCrudRepository`, `ReactiveMongoTemplate`, etc. |
| **Memory** | Loads all data into memory at once | Streams data as it's ready, more memory efficient |
| **Threading** | Uses the current thread until done | Works with event loop and scheduler threads |
| **Performance** | Can cause thread starvation on heavy I/O | Scales better with high-concurrency I/O workloads |
| **Error Handling** | Use `try/catch` | Use `.onErrorXxx()` (reactive operators) |
| **Backpressure** | Not supported | Supported natively |
| **Lazy vs Eager** | Eager execution | Lazy until subscribed |
| **Control over flow** | No flow control once started | Can cancel, buffer, or delay stream |
| **Good for** | Small to moderate datasets | Large datasets or high-concurrency environments |

---

## ✅ When to Use What?

| Scenario | Recommendation |
|----------|----------------|
| Simpler applications, teams new to async programming | ✅ Traditional Java |
| High-throughput, scalable APIs, microservices, real-time streams | ✅ Reactive Java |
| Heavy use of legacy or blocking libraries | ✅ Traditional Java |
| You want to scale horizontally with fewer resources | ✅ Reactive Java |

---