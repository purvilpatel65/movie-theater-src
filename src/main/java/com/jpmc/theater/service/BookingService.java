package com.jpmc.theater.service;

import com.jpmc.theater.exception.TheaterServiceException;
import com.jpmc.theater.model.Customer;
import com.jpmc.theater.model.Reservation;
import com.jpmc.theater.model.Showing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jpmc.theater.util.Constants.*;

public class BookingService {

    private DiscountService discountService;
    private Map<Integer, List<Reservation>> showingReservations;

    public BookingService() {
        discountService = new DiscountService();
        showingReservations = new HashMap<>();
    }

    /**
     * This method books tickets for given show and customer
     *
     * @param  customer  customer who is requesting the reservation
     * @param  show show for which the reservation is requested
     * @param  howManyTickets number of tickets needed for given reservation
     * @return reservation object reference for successfully made reservation
     */
    public Reservation bookTickets(Customer customer, Showing show, int howManyTickets)
            throws TheaterServiceException {

        if (show == null) throw new TheaterServiceException(INVALID_SHOW);

        List<Reservation> currentReservations = showingReservations.get(show.getShowId());
        int reservedSeats = countTotalReservedSeats(currentReservations);

        int availableSeats = show.getScreen().getTotalSeats() - reservedSeats;

        if (availableSeats == 0) throw new TheaterServiceException(SHOW_SOLD_OUT);
        if (availableSeats < howManyTickets) throw new TheaterServiceException(LIMITED_SEATS + availableSeats);

        double feePerTicket = show.getMovieFee() - discountService.calculateDiscount(show);
        double totalFee = feePerTicket * howManyTickets;

        Reservation reservation = new Reservation(customer, show, howManyTickets, totalFee);
        if (currentReservations == null) {
            currentReservations = new ArrayList<>();
            showingReservations.put(show.getShowId(), currentReservations);
        }
        currentReservations.add(reservation);

        return reservation;
    }

    /**
     * This method concludes reservations by removing them from list once show is over
     *
     * @param  show show for which the reservation needs to be concluded
     * @return void
     */
    public void concludeReservationsAfterShow(Showing show) {
        showingReservations.remove(show.getShowId());
    }

    /**
     * This method return list of all reservations for a given show
     *
     * @param  showId show id of the assocated reservations
     * @return list of all reservations for a given show
     */
    public List<Reservation> getAllReservationsForGivenShow(int showId) {
        return showingReservations.get(showId);
    }

    /**
     * This helper method helps in returning total reserved seats for a given show
     *
     * @param  currentReservations list of all reservation for given show
     * @return count of total reserved seats for given show
     */
    private int countTotalReservedSeats(List<Reservation> currentReservations) {
        if (currentReservations == null) return 0;

        AtomicInteger totalSeats = new AtomicInteger();
        currentReservations.forEach(reservation -> {
            totalSeats.addAndGet(reservation.getAudienceCount());
        });
        return totalSeats.get();
    }
}
