package com.hotel.pricer;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class PriceApiService {
    
    public CompletableFuture<Double> fetchBasePrice(String hotelId, LocalDate checkInDate, LocalDate checkOutDate, String currency) {
        if (hotelId == null || hotelId.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("hotelId must not be null or empty"));
        }
        if (checkInDate == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("checkInDate must not be null"));
        }
        if (checkOutDate == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("checkOutDate must not be null"));
        }
        if (currency == null || currency.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("currency must not be null or empty"));
        }
        if (!checkInDate.isBefore(checkOutDate)) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("checkInDate must be before checkOutDate"));
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Price API call interrupted", e);
            }
            
            double basePrice = calculateMockPrice(hotelId, checkInDate, checkOutDate);
            return basePrice;
        });
    }
    
    private double calculateMockPrice(String hotelId, LocalDate checkInDate, LocalDate checkOutDate) {
        int hotelHashCode = Math.abs(hotelId.hashCode() % 500);
        long nights = checkOutDate.toEpochDay() - checkInDate.toEpochDay();
        return (100 + hotelHashCode) * nights;
    }
}
