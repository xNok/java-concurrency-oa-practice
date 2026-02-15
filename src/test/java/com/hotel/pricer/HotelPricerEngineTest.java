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
        // Try multiple hotel IDs to find an unavailable one
        // The mock service returns unavailable ~20% of the time
        boolean foundUnavailable = false;
        
        for (int i = 0; i < 50; i++) {
            HotelPricingRequest request = new HotelPricingRequest(
                    "TEST" + i,
                    LocalDate.of(2026, 6, i % 27 + 1),
                    LocalDate.of(2026, 6, (i % 27 + 1) + 1),
                    "USD"
            );

            CompletableFuture<HotelPricingResponse> future = engine.getPricing(request);
            HotelPricingResponse response = future.get();

            if (!response.isAvailable()) {
                assertEquals(0.0, response.getBasePrice(), 0.01);
                assertEquals(0.0, response.getTax(), 0.01);
                assertEquals(0.0, response.getTotalPrice(), 0.01);
                foundUnavailable = true;
                break;
            }
        }
        
        assertTrue("Should have found at least one unavailable hotel in 50 attempts", foundUnavailable);
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
        HotelPricingRequest request1 = new HotelPricingRequest("US11111", LocalDate.of(2026, 3, 15), LocalDate.of(2026, 3, 16), "USD");
        HotelPricingRequest request2 = new HotelPricingRequest("EU22222", LocalDate.of(2026, 4, 10), LocalDate.of(2026, 4, 11), "EUR");
        HotelPricingRequest request3 = new HotelPricingRequest("JP33333", LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 2), "JPY");

        // Measure sequential processing time
        long sequentialStart = System.currentTimeMillis();
        engine.getPricing(request1).get();
        engine.getPricing(request2).get();
        engine.getPricing(request3).get();
        long sequentialEnd = System.currentTimeMillis();
        long sequentialDuration = sequentialEnd - sequentialStart;

        // Measure parallel processing time
        long parallelStart = System.currentTimeMillis();
        CompletableFuture<HotelPricingResponse> future1 = engine.getPricing(request1);
        CompletableFuture<HotelPricingResponse> future2 = engine.getPricing(request2);
        CompletableFuture<HotelPricingResponse> future3 = engine.getPricing(request3);

        CompletableFuture.allOf(future1, future2, future3).get();
        long parallelEnd = System.currentTimeMillis();
        long parallelDuration = parallelEnd - parallelStart;

        assertTrue("Parallel processing should be faster than sequential. Sequential: " + sequentialDuration + "ms, Parallel: " + parallelDuration + "ms",
                parallelDuration < sequentialDuration);
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

    @Test(expected = IllegalArgumentException.class)
    public void testNullHotelId() {
        new HotelPricingRequest(null, LocalDate.of(2026, 3, 15), LocalDate.of(2026, 3, 18), "USD");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyHotelId() {
        new HotelPricingRequest("", LocalDate.of(2026, 3, 15), LocalDate.of(2026, 3, 18), "USD");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCheckInDate() {
        new HotelPricingRequest("US12345", null, LocalDate.of(2026, 3, 18), "USD");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCheckOutDate() {
        new HotelPricingRequest("US12345", LocalDate.of(2026, 3, 15), null, "USD");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCurrency() {
        new HotelPricingRequest("US12345", LocalDate.of(2026, 3, 15), LocalDate.of(2026, 3, 18), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDateRange() {
        new HotelPricingRequest("US12345", LocalDate.of(2026, 3, 18), LocalDate.of(2026, 3, 15), "USD");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEqualDates() {
        new HotelPricingRequest("US12345", LocalDate.of(2026, 3, 15), LocalDate.of(2026, 3, 15), "USD");
    }

    @Test
    public void testNullRequest() throws ExecutionException, InterruptedException {
        try {
            engine.getPricing(null).get();
            fail("Should have thrown IllegalArgumentException");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }
}
