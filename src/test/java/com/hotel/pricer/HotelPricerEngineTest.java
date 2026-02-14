package com.hotel.pricer;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class HotelPricerEngineTest {
    private HotelPricerEngine engine;

    @Before
    public void setUp() {
        engine = new HotelPricerEngine();
    }

    @Test
    public void testGetPricingWithAvailableHotel() throws ExecutionException, InterruptedException {
        HotelPricingRequest request = new HotelPricingRequest(
                "US12345",
                LocalDate.of(2026, 3, 15),
                LocalDate.of(2026, 3, 18),
                "USD"
        );

        CompletableFuture<HotelPricingResponse> future = engine.getPricing(request);
        HotelPricingResponse response = future.get();

        assertNotNull(response);
        assertEquals("US12345", response.getHotelId());
        assertEquals("USD", response.getCurrency());
        assertTrue(response.getBasePrice() > 0);
        assertTrue(response.getTax() > 0);
        assertEquals(response.getBasePrice() + response.getTax(), response.getTotalPrice(), 0.01);
    }

    @Test
    public void testRegionalTaxRateUS() throws ExecutionException, InterruptedException {
        HotelPricingRequest request = new HotelPricingRequest(
                "US12345",
                LocalDate.of(2026, 3, 15),
                LocalDate.of(2026, 3, 16),
                "USD"
        );

        CompletableFuture<HotelPricingResponse> future = engine.getPricing(request);
        HotelPricingResponse response = future.get();

        if (response.isAvailable()) {
            double expectedTax = response.getBasePrice() * 0.10;
            assertEquals(expectedTax, response.getTax(), 0.01);
        }
    }

    @Test
    public void testRegionalTaxRateEU() throws ExecutionException, InterruptedException {
        HotelPricingRequest request = new HotelPricingRequest(
                "EU98765",
                LocalDate.of(2026, 4, 10),
                LocalDate.of(2026, 4, 11),
                "EUR"
        );

        CompletableFuture<HotelPricingResponse> future = engine.getPricing(request);
        HotelPricingResponse response = future.get();

        if (response.isAvailable()) {
            double expectedTax = response.getBasePrice() * 0.20;
            assertEquals(expectedTax, response.getTax(), 0.01);
        }
    }

    @Test
    public void testRegionalTaxRateJP() throws ExecutionException, InterruptedException {
        HotelPricingRequest request = new HotelPricingRequest(
                "JP11111",
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 2),
                "JPY"
        );

        CompletableFuture<HotelPricingResponse> future = engine.getPricing(request);
        HotelPricingResponse response = future.get();

        if (response.isAvailable()) {
            double expectedTax = response.getBasePrice() * 0.08;
            assertEquals(expectedTax, response.getTax(), 0.01);
        }
    }

    @Test
    public void testUnavailableHotel() throws ExecutionException, InterruptedException {
        for (int i = 0; i < 20; i++) {
            HotelPricingRequest request = new HotelPricingRequest(
                    "TEST" + i,
                    LocalDate.of(2026, 6, i + 1),
                    LocalDate.of(2026, 6, i + 2),
                    "USD"
            );

            CompletableFuture<HotelPricingResponse> future = engine.getPricing(request);
            HotelPricingResponse response = future.get();

            if (!response.isAvailable()) {
                assertEquals(0.0, response.getBasePrice(), 0.01);
                assertEquals(0.0, response.getTax(), 0.01);
                assertEquals(0.0, response.getTotalPrice(), 0.01);
                return;
            }
        }
    }

    @Test
    public void testMultipleNights() throws ExecutionException, InterruptedException {
        HotelPricingRequest request = new HotelPricingRequest(
                "US12345",
                LocalDate.of(2026, 3, 15),
                LocalDate.of(2026, 3, 20),
                "USD"
        );

        CompletableFuture<HotelPricingResponse> future = engine.getPricing(request);
        HotelPricingResponse response = future.get();

        assertTrue(response.getBasePrice() > 0);
    }

    @Test
    public void testParallelProcessing() throws ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();

        HotelPricingRequest request1 = new HotelPricingRequest("US11111", LocalDate.of(2026, 3, 15), LocalDate.of(2026, 3, 16), "USD");
        HotelPricingRequest request2 = new HotelPricingRequest("EU22222", LocalDate.of(2026, 4, 10), LocalDate.of(2026, 4, 11), "EUR");
        HotelPricingRequest request3 = new HotelPricingRequest("JP33333", LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 2), "JPY");

        CompletableFuture<HotelPricingResponse> future1 = engine.getPricing(request1);
        CompletableFuture<HotelPricingResponse> future2 = engine.getPricing(request2);
        CompletableFuture<HotelPricingResponse> future3 = engine.getPricing(request3);

        CompletableFuture.allOf(future1, future2, future3).get();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        assertTrue("Parallel processing should be faster than sequential", duration < 1500);
    }

    @Test
    public void testGetRegionalTaxRates() {
        assertNotNull(engine.getRegionalTaxRatesForTesting());
        assertTrue(engine.getRegionalTaxRatesForTesting().containsKey("US"));
        assertTrue(engine.getRegionalTaxRatesForTesting().containsKey("EU"));
        assertTrue(engine.getRegionalTaxRatesForTesting().containsKey("UK"));
        assertTrue(engine.getRegionalTaxRatesForTesting().containsKey("JP"));
        assertTrue(engine.getRegionalTaxRatesForTesting().containsKey("CA"));
        assertTrue(engine.getRegionalTaxRatesForTesting().containsKey("DEFAULT"));
    }
}
