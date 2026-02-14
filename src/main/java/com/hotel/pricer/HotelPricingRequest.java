package com.hotel.pricer;

import java.time.LocalDate;

public class HotelPricingRequest {
    private final String hotelId;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;
    private final String targetCurrency;

    public HotelPricingRequest(String hotelId, LocalDate checkInDate, LocalDate checkOutDate, String targetCurrency) {
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
