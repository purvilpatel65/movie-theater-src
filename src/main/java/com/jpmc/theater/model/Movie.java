package com.jpmc.theater.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@JsonRootName(value = "movie")
public class Movie {

    private int movieId;
    private String title;
    private double ticketPrice;
    private int specialCode;
    private Duration runningTime;

    public Movie() {
        this.movieId = UUID.randomUUID().hashCode();
    }

    public Movie(String title, Duration runningTime, double ticketPrice, int specialCode) {
        this();
        this.title = title;
        this.runningTime = runningTime;
        this.ticketPrice = ticketPrice;
        this.specialCode = specialCode;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public Duration getRunningTime() {
        return runningTime;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public int getSpecialCode() {
        return this.specialCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Double.compare(movie.ticketPrice, ticketPrice) == 0
                && Objects.equals(title, movie.title)
                && Objects.equals(runningTime, movie.runningTime)
                && Objects.equals(specialCode, movie.specialCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, runningTime, ticketPrice, specialCode);
    }
}