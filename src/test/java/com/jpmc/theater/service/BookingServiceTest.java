package com.jpmc.theater.service;

import com.jpmc.theater.exception.TheaterServiceException;
import com.jpmc.theater.model.*;
import com.jpmc.theater.util.LocalDateProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.jpmc.theater.util.Constants.*;


public class BookingServiceTest {

    /**
     * Test to validate a scenario when booking tickets operation is successful
     */
    @Test
    public void bookTickets_happyPath() throws TheaterServiceException {
        BookingService service = new BookingService();

        Customer c1 = new Customer("Zack");
        Screen s1 = new Screen(5);
        Movie m1 = new Movie("abc", Duration.ofMinutes(60), 10.0, 0);
        Showing show = new Showing(m1, s1, 1,
                LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(9, 0)));

        Reservation reservation = service.bookTickets(c1, show, 3);

        Assertions.assertNotNull(reservation);

        //validate mapping
        Assertions.assertEquals(show, reservation.getShow());
        Assertions.assertEquals(3, reservation.getAudienceCount());
        Assertions.assertEquals(21.0, reservation.getTotalFee()); //validate discounted price
    }

    /**
     * Test to validate a scenario when limited seats available then required by client
     */
    @Test
    public void bookTickets_LimitedSeatsAvailableThanRequired() {
        BookingService service = new BookingService();

        Customer c1 = new Customer("Zack");
        Screen s1 = new Screen(5);
        Movie m1 = new Movie("abc", Duration.ofMinutes(60), 10.0, 0);
        Showing show = new Showing(m1, s1, 1,
                LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(9, 0)));

        String errorMsg = "";
        try {
            Reservation r1 = service.bookTickets(c1, show, 3); //this reservation will be success
            Reservation r2 = service.bookTickets(c1, show, 3); //this reservation will be fail
        } catch (TheaterServiceException e) {
            errorMsg = e.getMessage();
        }

        Assertions.assertEquals(LIMITED_SEATS + 2, errorMsg);
        Assertions.assertEquals(1, service.getAllReservationsForGivenShow(show.getShowId()).size());
    }

    /**
     * Test to validate a scenario when show is sold out
     */
    @Test
    public void bookTickets_ShowSoldOut() {
        BookingService service = new BookingService();

        Customer c1 = new Customer("Zack");
        Screen s1 = new Screen(3);
        Movie m1 = new Movie("abc", Duration.ofMinutes(60), 10.0, 0);
        Showing show = new Showing(m1, s1, 1,
                LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(9, 0)));

        String errorMsg = "";
        try {
            Reservation r1 = service.bookTickets(c1, show, 3); //this reservation will be success
            Reservation r2 = service.bookTickets(c1, show, 1); //this reservation will be fail
        } catch (TheaterServiceException e) {
            errorMsg = e.getMessage();
        }

        Assertions.assertEquals(SHOW_SOLD_OUT, errorMsg);
        Assertions.assertEquals(1, service.getAllReservationsForGivenShow(show.getShowId()).size());
    }

    /**
     * Test to validate a scenario when invalid show is provided while making a reservation
     */
    @Test
    public void bookTickets_InvalidShow() {
        BookingService service = new BookingService();

        Customer c1 = new Customer("Zack");
        Screen s1 = new Screen(3);
        Movie m1 = new Movie("abc", Duration.ofMinutes(60), 10.0, 0);

        String errorMsg = "";
        try {
            Reservation r1 = service.bookTickets(c1, null, 3);
        } catch (TheaterServiceException e) {
            errorMsg = e.getMessage();
        }

        Assertions.assertEquals(INVALID_SHOW, errorMsg);
    }

    /**
     * Test to validate a scenario when all reservations gets removed successfully from the list once the show ends
     */
    @Test
    public void concludeReservationsAfterShow_SuccessfulCleanup() throws TheaterServiceException {
        BookingService service = new BookingService();

        Customer c1 = new Customer("Zack");
        Screen s1 = new Screen(5);
        Movie m1 = new Movie("abc", Duration.ofMinutes(60), 10.0, 0);
        Movie m2 = new Movie("xyz", Duration.ofMinutes(60), 10.0, 0);
        Showing show1 = new Showing(m1, s1, 1,
                LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(9, 0)));
        Showing show2 = new Showing(m2, s1, 1,
                LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(12, 0)));

        Reservation r1 = service.bookTickets(c1, show1, 2);
        Reservation r2 = service.bookTickets(c1, show1, 2);
        Reservation r3 = service.bookTickets(c1, show2, 2);

        service.concludeReservationsAfterShow(show1);
        Assertions.assertNull(service.getAllReservationsForGivenShow(show1.getShowId()));
    }
}
