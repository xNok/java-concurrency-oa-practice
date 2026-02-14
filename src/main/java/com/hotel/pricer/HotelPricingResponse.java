package com.hotel.pricer;

public class HotelPricingResponse {
    private final String hotelId;
    private final boolean available;
    private final double basePrice;
    private final double tax;
    private final double totalPrice;
    private final String currency;

    public HotelPricingResponse(String hotelId, boolean available, double basePrice, double tax, double totalPrice, String currency) {
        this.hotelId = hotelId;
        this.available = available;
        this.basePrice = basePrice;
        this.tax = tax;
        this.totalPrice = totalPrice;
        this.currency = currency;
    }

    public String getHotelId() {
        return hotelId;
    }

    public boolean isAvailable() {
        return available;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public double getTax() {
        return tax;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "HotelPricingResponse{" +
                "hotelId='" + hotelId + '\'' +
                ", available=" + available +
                ", basePrice=" + basePrice +
                ", tax=" + tax +
                ", totalPrice=" + totalPrice +
                ", currency='" + currency + '\'' +
                '}';
    }
}
