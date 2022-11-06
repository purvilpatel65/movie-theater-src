package com.jpmc.theater.service;

import com.jpmc.theater.model.Movie;
import com.jpmc.theater.model.Showing;

import java.time.LocalDateTime;

import static com.jpmc.theater.util.Constants.SPECIAL_MOVIE_CODE;

public class DiscountService {

    public DiscountService() {}

    /**
     * This method calculates discount for a given show based on movie specialty, show sequence, show timing and show date.
     * If multiple discount rules apply for given show, then it will return discount with highest amount
     *
     * @param  show show to calculate discount for
     * @return discount amount
     */
    public double calculateDiscount(Showing show) {
        Movie movie = show.getMovie();
        double discount = 0;

        discount = Math.max(discount,
                calculateSpecialDiscount(movie.getSpecialCode(), movie.getTicketPrice()));
        discount = Math.max(discount,
                calculateSequenceDiscount(show.getSequenceOfTheDay()));
        discount = Math.max(discount,
                calculateTimingDiscount(show.getStartTime(), movie.getTicketPrice()));
        discount = Math.max(discount,
                calculateDayDiscount(show.getStartTime()));

        return discount;
    }

    /**
     * This helper method helps in calculating show discount based on movie specialty
     *
     * @param  specialCode movie special code
     * @param  ticketPrice price of a ticket for given movie
     * @return discount amount
     */
    private double calculateSpecialDiscount(int specialCode, double ticketPrice) {
        double specialDiscount = 0;
        if (specialCode == SPECIAL_MOVIE_CODE) {
            specialDiscount = ticketPrice * 0.2;
        }
        return specialDiscount;
    }

    /**
     * This helper method helps in calculating show discount based on sequence of the show
     *
     * @param  sequence sequence of the show on a given day
     * @return discount amount
     */
    private double calculateSequenceDiscount(int sequence) {
        double sequenceDiscount = 0;

        switch (sequence) {
            case 1:
                sequenceDiscount = 3.0;
                break;
            case 2:
                sequenceDiscount = 2.0;
                break;
        }
        return sequenceDiscount;
    }

    /**
     * This helper method helps in calculating show discount based on timing of the show
     *
     * @param  startTime start date and time of given show
     * @param  ticketPrice price of a ticket for given movie
     * @return discount amount
     */
    private double calculateTimingDiscount(LocalDateTime startTime, double ticketPrice) {
        double timingDiscount = 0;
        int startHour = startTime.getHour();

        if (startHour >= 11 && startHour <= 16) {
            timingDiscount = ticketPrice * 0.25;
        }
        return timingDiscount;
    }

    /**
     * This helper method helps in calculating show discount based on day of the show
     *
     * @param  startTime start date and time of given show
     * @return discount amount
     */
    private double calculateDayDiscount(LocalDateTime startTime) {
        double dayDiscount = 0;
        int day = startTime.getDayOfMonth();

        switch (day) {
            case 7:
                dayDiscount = 1.0;
                break;
        }
        return dayDiscount;
    }
}
