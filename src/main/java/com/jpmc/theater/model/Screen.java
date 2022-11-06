package com.jpmc.theater.model;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.UUID;

@JsonRootName(value = "screen")
public class Screen {

    private int screenId;
    private int totalSeats;

    public Screen() {
        this.screenId = UUID.randomUUID().hashCode();
    }

    public Screen(int totalSeats) {
        this();
        this.totalSeats = totalSeats;
    }

    public int getScreenId() {
        return this.screenId;
    }

    public int getTotalSeats() {
        return this.totalSeats;
    }
}
