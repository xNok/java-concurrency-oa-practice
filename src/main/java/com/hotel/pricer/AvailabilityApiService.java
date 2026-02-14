package com.hotel.pricer;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AvailabilityApiService {
    
    public CompletableFuture<Boolean> checkAvailability(String hotelId, LocalDate checkInDate, LocalDate checkOutDate) {
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
