package com.jpmc.theater.model;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "reservation")
public class Reservation {
    private Customer customer;
    private Showing showing;
    private int audienceCount;
    private double totalFee;

    public Reservation(Customer customer, Showing showing, int audienceCount, double totalFee) {
        this.customer = customer;
        this.showing = showing;
        this.audienceCount = audienceCount;
        this.totalFee = totalFee;
    }

    public double getTotalFee() {
        return this.totalFee;
    }

    public Showing getShow() {
        return this.showing;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public int getAudienceCount() {
        return audienceCount;
    }

    @Override
    public String toString() {
        return "Customer Name - " + this.getCustomer().getName()
                + " : Movie - " + this.getShow().getMovie().getTitle()
                + " : Date&Time - " + this.getShow().getStartTime()
                + " : Total tickets - " + this.getAudienceCount()
                + " : Total fee - $" + this.getTotalFee();
    }
}