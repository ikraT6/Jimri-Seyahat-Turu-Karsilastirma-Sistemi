package com.travel;

public class Offer {

    private String company;
    private double flightPrice;
    private double hotelPrice;
    private double totalPrice;

    public Offer(String company, double flightPrice,
                 double hotelPrice, double totalPrice) {
        this.company = company;
        this.flightPrice = flightPrice;
        this.hotelPrice = hotelPrice;
        this.totalPrice = totalPrice;
    }

    public String getCompany() {
        return company;
    }

    public double getFlightPrice() {
        return flightPrice;
    }

    public double getHotelPrice() {
        return hotelPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
