package com.hotel.pricer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HotelPricerEngine {
    private final PriceApiService priceApiService;
    private final AvailabilityApiService availabilityApiService;
    private final Map<String, Double> regionalTaxRates;

    public HotelPricerEngine() {
        this.priceApiService = new PriceApiService();
        this.availabilityApiService = new AvailabilityApiService();
        this.regionalTaxRates = initializeRegionalTaxRates();
    }

    public HotelPricerEngine(PriceApiService priceApiService, AvailabilityApiService availabilityApiService) {
        this.priceApiService = priceApiService;
        this.availabilityApiService = availabilityApiService;
        this.regionalTaxRates = initializeRegionalTaxRates();
    }

    private Map<String, Double> initializeRegionalTaxRates() {
        Map<String, Double> taxRates = new HashMap<>();
        taxRates.put("US", 0.10);
        taxRates.put("EU", 0.20);
        taxRates.put("UK", 0.15);
        taxRates.put("JP", 0.08);
        taxRates.put("CA", 0.12);
        taxRates.put("DEFAULT", 0.05);
        return taxRates;
    }

    public CompletableFuture<HotelPricingResponse> getPricing(HotelPricingRequest request) {
        if (request == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("HotelPricingRequest must not be null"));
        }
        
        CompletableFuture<Double> priceFuture = priceApiService.fetchBasePrice(
                request.getHotelId(),
                request.getCheckInDate(),
                request.getCheckOutDate(),
                request.getTargetCurrency()
        );

        CompletableFuture<Boolean> availabilityFuture = availabilityApiService.checkAvailability(
                request.getHotelId(),
                request.getCheckInDate(),
                request.getCheckOutDate()
        );

        return CompletableFuture.allOf(priceFuture, availabilityFuture)
                .thenApply(v -> {
                    double basePrice = priceFuture.join();
                    boolean isAvailable = availabilityFuture.join();

                    if (!isAvailable) {
                        return new HotelPricingResponse(
                                request.getHotelId(),
                                false,
                                0.0,
                                0.0,
                                0.0,
                                request.getTargetCurrency()
                        );
                    }

                    String region = extractRegionFromHotelId(request.getHotelId());
                    double taxRate = regionalTaxRates.getOrDefault(region, regionalTaxRates.get("DEFAULT"));
                    double tax = basePrice * taxRate;
                    double totalPrice = basePrice + tax;

                    return new HotelPricingResponse(
                            request.getHotelId(),
                            true,
                            basePrice,
                            tax,
                            totalPrice,
                            request.getTargetCurrency()
                    );
                });
    }

    private String extractRegionFromHotelId(String hotelId) {
        if (hotelId == null || hotelId.length() < 2) {
            return "DEFAULT";
        }
        
        String regionCode = hotelId.substring(0, 2).toUpperCase();
        // Return the region code if configured, otherwise will fall back to DEFAULT via getOrDefault
        return regionCode;
    }

    Map<String, Double> getRegionalTaxRatesForTesting() {
        return new HashMap<>(regionalTaxRates);
    }
}
