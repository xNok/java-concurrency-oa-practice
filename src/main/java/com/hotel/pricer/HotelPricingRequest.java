package com.hotel.pricer;

import java.time.LocalDate;

public class HotelPricingRequest {
    private final String hotelId;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;
    private final String targetCurrency;

    public HotelPricingRequest(String hotelId, LocalDate checkInDate, LocalDate checkOutDate, String targetCurrency) {
        if (hotelId == null || hotelId.trim().isEmpty()) {
            throw new IllegalArgumentException("hotelId must not be null or empty");
        }
        if (checkInDate == null) {
            throw new IllegalArgumentException("checkInDate must not be null");
        }
        if (checkOutDate == null) {
            throw new IllegalArgumentException("checkOutDate must not be null");
        }
        if (targetCurrency == null || targetCurrency.trim().isEmpty()) {
            throw new IllegalArgumentException("targetCurrency must not be null or empty");
        }
        if (!checkInDate.isBefore(checkOutDate)) {
            throw new IllegalArgumentException("checkInDate must be before checkOutDate");
        }
        
        this.hotelId = hotelId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.targetCurrency = targetCurrency;
    }

    public String getHotelId() {
        return hotelId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }
}
