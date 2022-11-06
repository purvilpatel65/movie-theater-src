package com.jpmc.theater;

import com.jpmc.theater.exception.TheaterServiceException;
import com.jpmc.theater.model.*;
import com.jpmc.theater.service.BookingService;
import com.jpmc.theater.service.TheaterManagementService;
import com.jpmc.theater.util.LocalDateProvider;
import com.jpmc.theater.util.PrintUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class Theater {

    public static void main(String[] args) {
        TheaterManagementService theaterService = new TheaterManagementService();
        BookingService bookingService = new BookingService();

        try {

            //Adding new screens int the theater
            Screen s1 = theaterService.addNewScreen(10);
            Screen s2 = theaterService.addNewScreen(5);

            //Adding new movies
            Optional<Movie> m1 = theaterService.addNewMovie("Spider-Man: No Way Home", Duration.ofMinutes(90), 12.5, 1);
            Optional<Movie> m2 = theaterService.addNewMovie("Turning Red", Duration.ofMinutes(60), 11, 0);
            Optional<Movie> m3 = theaterService.addNewMovie("The Dark Knight Rises", Duration.ofMinutes(60), 13.50, 0);

            //Adding new shows
            Optional<Showing> show1 = null;
            Optional<Showing> show2 = null;
            Optional<Showing> show3 = null;
            Optional<Showing> show4 = null;
            Optional<Showing> show5 = null;
            Optional<Showing> show6 = null;
            Optional<Showing> show7 = null;
            Optional<Showing> show8 = null;
            Optional<Showing> show9 = null;
            if (m1.isPresent()) {
                show1 = theaterService.addNewShow(s1, m1.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(9, 0)));
                show2 = theaterService.addNewShow(s1, m1.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(11, 0)));
                show3 = theaterService.addNewShow(s2, m1.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(9, 0)));
            }
            if (m2.isPresent()) {
                show4 = theaterService.addNewShow(s1, m2.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(13, 0)));
                show5 = theaterService.addNewShow(s2, m2.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(11, 0)));
                show6 = theaterService.addNewShow(s2, m2.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(13, 0)));
            }
            if (m3.isPresent()) {
                show7 = theaterService.addNewShow(s1, m3.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(15, 0)));
                show8 = theaterService.addNewShow(s1, m3.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(19, 0)));
                show9 = theaterService.addNewShow(s2, m3.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(17, 0)));
            }

            //Printing schedule
            List<Showing> allShowsForScreenS1 = theaterService.getAllShowsForGivenScreen(s1.getScreenId());
            PrintUtils.printSimpleFormat(allShowsForScreenS1, "Schedule");
            PrintUtils.printJsonFormat(allShowsForScreenS1, "Schedule");

            //Creating customers
            Customer c1 = new Customer("Zoe");
            Customer c2 = new Customer("Alex");

            //Reserving shows
            if (show1.isPresent()) {
                bookingService.bookTickets(c1, show1.get(), 2);
                bookingService.bookTickets(c2, show1.get(), 3);
            }

            if (show2.isPresent()) {
                bookingService.bookTickets(c1, show2.get(), 2);
                bookingService.bookTickets(c2, show2.get(), 2);
            }

            //Printing reservations
            List<Reservation> allReservationsForShow1 = bookingService.getAllReservationsForGivenShow(show1.get().getShowId());
            PrintUtils.printSimpleFormat(allReservationsForShow1, "Reservations");
            PrintUtils.printJsonFormat(allReservationsForShow1, "Reservations");

        } catch (TheaterServiceException e) {
            System.out.println("Error - " + e.getMessage());;
        }
    }
}
