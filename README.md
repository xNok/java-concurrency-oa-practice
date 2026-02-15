# Global Hotel Pricer

A Java-based practice project demonstrating concurrent programming concepts through a hotel pricing engine.

> **📚 For Students:**
> - 📝 [ASSIGNMENT.md](ASSIGNMENT.md) - Complete task description, requirements, and implementation guide
> - 🎓 [LESSONS.md](LESSONS.md) - Key lessons, best practices, and real-world applications
> - 💻 This README - Reference implementation and quick start guide

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Quick Start](#quick-start)
- [Architecture](#architecture)
- [Testing](#testing)
- [License](#license)

## Overview

The Global Hotel Pricer is a demonstration of parallel API calls and concurrent processing in Java. It simulates fetching hotel pricing data by making parallel calls to mock external services.

## Features

- **Parallel API Calls**: Fetches base price and availability information concurrently using `CompletableFuture`
- **Regional Tax Calculation**: Applies region-specific tax rates based on hotel location
- **Mock Services**: Simulates external Price API and Availability API services
- **Multi-currency Support**: Handles pricing requests in different currencies (USD, EUR, JPY, etc.)

## Requirements

Input:
- Hotel ID
- Check-in and check-out dates (stay dates)
- Target currency

Output:
- Hotel availability status
- Base price
- Tax amount
- Total price
- Currency

## Architecture

### Components

1. **HotelPricerEngine**: The main engine that orchestrates parallel API calls
2. **PriceApiService**: Mock service that returns base pricing information
3. **AvailabilityApiService**: Mock service that checks hotel availability
4. **Regional Tax Map**: Configuration mapping regions to their tax rates

### Regional Tax Rates

| Region | Tax Rate |
|--------|----------|
| US     | 10%      |
| EU     | 20%      |
| UK     | 15%      |
| JP     | 8%       |
| CA     | 12%      |
| DEFAULT| 5%       |

## Building the Project

## Quick Start

### Prerequisites

- Java 11 or higher
- Maven 3.x

### Build

```bash
mvn clean compile
```

### Run Tests

```bash
mvn test
```

### Run Demo Application

```bash
mvn exec:java -Dexec.mainClass="com.hotel.pricer.GlobalHotelPricerApp"
```

## Usage Example

```java
HotelPricerEngine engine = new HotelPricerEngine();

HotelPricingRequest request = new HotelPricingRequest(
    "US12345",                          // Hotel ID
    LocalDate.of(2026, 3, 15),         // Check-in date
    LocalDate.of(2026, 3, 18),         // Check-out date
    "USD"                               // Target currency
);

CompletableFuture<HotelPricingResponse> future = engine.getPricing(request);
HotelPricingResponse response = future.get();

System.out.println(response);
```

## Implementation Details

### Parallel Processing

The engine uses Java's `CompletableFuture` API to make parallel calls:

1. Initiates price fetch and availability check simultaneously
2. Waits for both operations to complete using `CompletableFuture.allOf()`
3. Combines results and applies regional tax
4. Returns final pricing response

### Mock Service Behavior

- **PriceApiService**: Calculates price based on hotel ID hash and number of nights (simulates ~100ms delay)
- **AvailabilityApiService**: Determines availability based on hotel ID and date hash (simulates ~100ms delay)
- Both services use `CompletableFuture.supplyAsync()` to simulate asynchronous operations

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/
│           └── hotel/
│               └── pricer/
│                   ├── AvailabilityApiService.java
│                   ├── GlobalHotelPricerApp.java
│                   ├── HotelPricerEngine.java
│                   ├── HotelPricingRequest.java
│                   ├── HotelPricingResponse.java
│                   └── PriceApiService.java
└── test/
    └── java/
        └── com/
            └── hotel/
                └── pricer/
                    └── HotelPricerEngineTest.java
```

## Testing

The project includes comprehensive unit tests covering:
- Basic pricing functionality
- Regional tax calculations for different regions
- Unavailable hotel handling
- Multi-night stays
- Parallel processing performance
- Regional tax rate configuration

Run tests with:
```bash
mvn test
```

## License

Apache License 2.0
