package com.hotel.pricer;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class PriceApiService {
    
    public CompletableFuture<Double> fetchBasePrice(String hotelId, LocalDate checkInDate, LocalDate checkOutDate, String currency) {
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
