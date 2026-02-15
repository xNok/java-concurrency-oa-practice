package com.hotel.pricer;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AvailabilityApiService {
    
    public CompletableFuture<Boolean> checkAvailability(String hotelId, LocalDate checkInDate, LocalDate checkOutDate) {
        if (hotelId == null || hotelId.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("hotelId must not be null or empty"));
        }
        if (checkInDate == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("checkInDate must not be null"));
        }
        if (checkOutDate == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("checkOutDate must not be null"));
        }
        if (!checkInDate.isBefore(checkOutDate)) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("checkInDate must be before checkOutDate"));
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Availability API call interrupted", e);
            }
            
            return isAvailable(hotelId, checkInDate);
        });
    }
    
    private boolean isAvailable(String hotelId, LocalDate checkInDate) {
        int hash = Math.abs((hotelId + checkInDate.toString()).hashCode());
        return hash % 10 < 8;
    }
}
