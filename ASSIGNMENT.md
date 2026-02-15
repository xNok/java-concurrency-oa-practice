# Assignment: The Global Hotel Pricer

## 🎯 Learning Objectives

By completing this assignment, you will:

1. **Master Concurrent Programming**: Learn to use Java's `CompletableFuture` API for parallel execution
2. **Understand Asynchronous APIs**: Work with non-blocking operations and future-based result handling
3. **Apply Real-World Patterns**: Implement a common pattern used in microservices architectures
4. **Handle Race Conditions**: Properly coordinate multiple concurrent operations
5. **Write Thread-Safe Code**: Ensure data consistency in a multi-threaded environment

## 📋 Task Description

You are tasked with building a **Hotel Pricing Engine** that fetches pricing information from multiple external services. In a real-world scenario, these would be separate microservices or external APIs.

### Requirements

Your pricing engine must:

1. **Accept the following inputs:**
   - Hotel ID (String)
   - Check-in date (LocalDate)
   - Check-out date (LocalDate)
   - Target currency (String)

2. **Make two external API calls IN PARALLEL:**
   - Call the Price API to fetch the base price
   - Call the Availability API to check if the hotel is available

3. **Apply regional tax:**
   - Extract the region from the Hotel ID (first 2 characters)
   - Look up the tax rate from a pre-configured Map
   - Calculate: `totalPrice = basePrice + (basePrice * taxRate)`

4. **Return a comprehensive response:**
   - Hotel ID
   - Availability status
   - Base price
   - Tax amount
   - Total price
   - Currency

### Regional Tax Rates

| Region Code | Tax Rate |
|-------------|----------|
| US          | 10%      |
| EU          | 20%      |
| UK          | 15%      |
| JP          | 8%       |
| CA          | 12%      |
| DEFAULT     | 5%       |

## 🔑 Key Concepts

### 1. CompletableFuture

`CompletableFuture` is Java's way of handling asynchronous operations. Think of it as a "promise" that a result will be available in the future.

**Key Methods:**
- `CompletableFuture.supplyAsync()`: Execute a task asynchronously
- `CompletableFuture.allOf()`: Wait for multiple futures to complete
- `.thenApply()`: Transform the result once available
- `.join()`: Block and retrieve the result

### 2. Parallel vs Sequential Execution

**Sequential (Slow):**
```java
// This takes 200ms total
double price = priceApi.fetch();      // 100ms
boolean available = availApi.check(); // 100ms
```

**Parallel (Fast):**
```java
// This takes only ~100ms total!
CompletableFuture<Double> priceFuture = CompletableFuture.supplyAsync(() -> priceApi.fetch());
CompletableFuture<Boolean> availFuture = CompletableFuture.supplyAsync(() -> availApi.check());
CompletableFuture.allOf(priceFuture, availFuture).join();
```

### 3. Mock Services

In this assignment, you'll create mock services that simulate real API calls:
- Add a small delay (e.g., `TimeUnit.MILLISECONDS.sleep(100)`)
- Return deterministic results based on input parameters
- Wrap everything in `CompletableFuture.supplyAsync()`

## 💡 Implementation Hints

### Step 1: Create Domain Models

Start by defining your request and response objects:
```java
public class HotelPricingRequest {
    private final String hotelId;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;
    private final String targetCurrency;
    // Constructor, getters
}
```

### Step 2: Build Mock Services

Create services that return `CompletableFuture`:
```java
public class PriceApiService {
    public CompletableFuture<Double> fetchBasePrice(String hotelId, ...) {
        return CompletableFuture.supplyAsync(() -> {
            // Simulate network delay
            // Calculate mock price
            // Return result
        });
    }
}
```

### Step 3: Implement the Engine

The engine orchestrates everything:
```java
public CompletableFuture<HotelPricingResponse> getPricing(HotelPricingRequest request) {
    // 1. Start both API calls in parallel
    CompletableFuture<Double> priceFuture = ...;
    CompletableFuture<Boolean> availFuture = ...;
    
    // 2. Wait for both to complete
    return CompletableFuture.allOf(priceFuture, availFuture)
        .thenApply(v -> {
            // 3. Get results
            // 4. Apply tax
            // 5. Build response
        });
}
```

### Step 4: Handle Edge Cases

Consider these scenarios:
- What if the hotel is unavailable? (Return 0.0 for all price fields)
- What if the region code is not in the tax map? (Use DEFAULT rate)
- What if API calls fail? (Handle exceptions appropriately)

## 🧪 Testing Strategy

### Unit Tests to Write

1. **Happy Path Test**: Verify basic pricing calculation
2. **Tax Rate Tests**: Test each regional tax rate
3. **Unavailable Hotel Test**: Ensure correct handling when hotel isn't available
4. **Parallel Execution Test**: Verify that calls happen in parallel (check execution time)
5. **Edge Cases**: Unknown regions, invalid inputs, etc.

### Performance Testing

Your parallel implementation should be roughly **2x faster** than sequential:
```java
@Test
public void testParallelProcessing() {
    long start = System.currentTimeMillis();
    // Make multiple concurrent pricing requests
    long duration = System.currentTimeMillis() - start;
    
    // Should complete in ~100ms, not 200ms
    assertTrue(duration < 1500); // Account for CI environment
}
```

## 📚 Key Lessons

### Lesson 1: Why Parallel Processing Matters

In microservices architectures, services often need to fetch data from multiple sources. Making these calls in parallel can dramatically improve response times.

**Example:** If you need data from 5 services, each taking 100ms:
- Sequential: 500ms total
- Parallel: ~100ms total (5x faster!)

### Lesson 2: Understanding Futures

A `Future` represents a computation that may not have completed yet. This allows your program to:
- Start multiple operations simultaneously
- Continue doing other work
- Retrieve results when needed

### Lesson 3: Thread Safety

When working with concurrent code, be careful about:
- **Immutable objects**: Prefer final fields and immutable classes
- **Thread-safe collections**: Use `ConcurrentHashMap` if needed
- **Atomic operations**: Be cautious with shared mutable state

### Lesson 4: Error Handling in Async Code

Exceptions in asynchronous code need special handling:
```java
CompletableFuture.supplyAsync(() -> {
    try {
        // Your code
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException("Operation interrupted", e);
    }
});
```

### Lesson 5: Real-World Applications

This pattern is used everywhere:
- **E-commerce**: Fetch product info, inventory, pricing in parallel
- **Travel booking**: Check flights, hotels, car rentals simultaneously
- **Social media**: Load profile, posts, friends concurrently
- **Banking**: Verify balance, check limits, log transaction in parallel

## 🚀 Bonus Challenges

Once you complete the basic implementation, try these:

1. **Add Retry Logic**: Retry failed API calls up to 3 times
2. **Implement Timeouts**: Fail fast if an API takes too long
3. **Add Caching**: Cache frequently requested hotel prices
4. **Circuit Breaker**: Stop calling failing services temporarily
5. **Batch Processing**: Process multiple pricing requests in parallel
6. **Add Metrics**: Track success rate, average latency, etc.

## ❓ Common Pitfalls

### 1. Blocking in Async Code
```java
// ❌ Bad: This blocks and defeats the purpose
CompletableFuture.supplyAsync(() -> someExpensiveCall()).join();
```

### 2. Not Handling Interruption
```java
// ❌ Bad: Swallows interrupt signal
catch (InterruptedException e) {
    e.printStackTrace(); // Wrong!
}

// ✅ Good: Preserves interrupt status
catch (InterruptedException e) {
    Thread.currentThread().interrupt();
    throw new RuntimeException(e);
}
```

### 3. Forgetting to Wait for All Futures
```java
// ❌ Bad: Only waits for first future
CompletableFuture<A> f1 = ...;
CompletableFuture<B> f2 = ...;
return f1.thenApply(...); // f2 might not be done!

// ✅ Good: Wait for both
return CompletableFuture.allOf(f1, f2).thenApply(...);
```

## 📖 Additional Resources

- [Java CompletableFuture Guide](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html)
- [Asynchronous Programming Patterns](https://en.wikipedia.org/wiki/Asynchronous_method_invocation)
- [Microservices Best Practices](https://microservices.io/)

## ✅ Evaluation Criteria

Your solution will be evaluated on:

1. **Correctness** (40%): Does it produce correct results?
2. **Concurrency** (30%): Are API calls truly parallel?
3. **Code Quality** (15%): Clean, readable, well-structured code?
4. **Testing** (10%): Comprehensive test coverage?
5. **Error Handling** (5%): Proper exception handling?

## 🎓 Success Criteria

You've successfully completed the assignment when:

- ✅ All unit tests pass
- ✅ Parallel processing is demonstrably faster than sequential
- ✅ Regional taxes are correctly applied
- ✅ Edge cases are properly handled
- ✅ Code is clean and well-documented
- ✅ No race conditions or thread safety issues

Good luck! Remember: the goal is not just to complete the task, but to deeply understand concurrent programming patterns that you'll use throughout your career.
