package com.jpmc.theater.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.jpmc.theater.util.PrintUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonRootName(value = "show")
public class Showing {

    private int showId;
    private Movie movie;
    private Screen screen;
    private int sequenceOfTheDay;
    private LocalDateTime showStartTime;

    public Showing() {
        showId = UUID.randomUUID().hashCode();
    }

    public Showing(Movie movie, Screen screen, int sequenceOfTheDay, LocalDateTime showStartTime) {
        this();
        this.movie = movie;
        this.screen = screen;
        this.sequenceOfTheDay = sequenceOfTheDay;
        this.showStartTime = showStartTime;
    }

    public int getShowId() {
        return this.showId;
    }


    public Movie getMovie() {
        return this.movie;
    }

    public Screen getScreen() {
        return this.screen;
    }

    public LocalDateTime getStartTime() {
        return this.showStartTime;
    }

    public double getMovieFee() {
        return this.movie.getTicketPrice();
    }

    public int getSequenceOfTheDay() {
        return this.sequenceOfTheDay;
    }

    public int setSequenceOfTheDay(int sequence) {
        return this.sequenceOfTheDay = sequence;
    }

    @Override
    public String toString() {
        return "Sequence - " + getSequenceOfTheDay()
                + " : Start Time - " + this.getStartTime()
                + " : Movie Title - " + this.getMovie().getTitle()
                + " : Duration - " + PrintUtils.humanReadableFormat(this.getMovie().getRunningTime())
                + " : Fee - $" + this.getMovieFee();
    }
}
