package com.hotel.pricer;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GlobalHotelPricerApp {
    
    public static void main(String[] args) {
        HotelPricerEngine engine = new HotelPricerEngine();
        
        System.out.println("=== Global Hotel Pricer Demo ===\n");
        
        HotelPricingRequest request1 = new HotelPricingRequest(
                "US12345",
                LocalDate.of(2026, 3, 15),
                LocalDate.of(2026, 3, 18),
                "USD"
        );
        
        HotelPricingRequest request2 = new HotelPricingRequest(
                "EU98765",
                LocalDate.of(2026, 4, 10),
                LocalDate.of(2026, 4, 12),
                "EUR"
        );
        
        HotelPricingRequest request3 = new HotelPricingRequest(
                "JP11111",
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 5),
                "JPY"
        );
        
        try {
            System.out.println("Processing pricing requests in parallel...\n");
            
            CompletableFuture<HotelPricingResponse> future1 = engine.getPricing(request1);
            CompletableFuture<HotelPricingResponse> future2 = engine.getPricing(request2);
            CompletableFuture<HotelPricingResponse> future3 = engine.getPricing(request3);
            
            CompletableFuture.allOf(future1, future2, future3).get();
            
            System.out.println("Request 1 (US Hotel):");
            System.out.println(future1.get());
            System.out.println();
            
            System.out.println("Request 2 (EU Hotel):");
            System.out.println(future2.get());
            System.out.println();
            
            System.out.println("Request 3 (JP Hotel):");
            System.out.println(future3.get());
            System.out.println();
            
            System.out.println("=== Regional Tax Rates ===");
            engine.getRegionalTaxRatesForTesting().forEach((region, rate) -> 
                System.out.println(region + ": " + (rate * 100) + "%")
            );
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error processing pricing requests: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
