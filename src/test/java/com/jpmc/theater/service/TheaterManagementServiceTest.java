package com.jpmc.theater.service;

import com.jpmc.theater.exception.TheaterServiceException;
import com.jpmc.theater.model.Movie;
import com.jpmc.theater.model.Screen;
import com.jpmc.theater.model.ServiceResponse;
import com.jpmc.theater.model.Showing;
import com.jpmc.theater.util.LocalDateProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.jpmc.theater.util.Constants.NO_MOVIES;
import static com.jpmc.theater.util.Constants.NO_SCREEN;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TheaterManagementServiceTest {

    /**
     * Test to validate a scenario when adding a new movie in the theater is successfull
     */
    @Test
    public void addNewMovie_happyPath() {
        TheaterManagementService service = new TheaterManagementService();
        Optional<Movie> m1 = service.addNewMovie("abc", Duration.ofMinutes(60), 10.0, 1);

        Assertions.assertTrue(m1.isPresent());
        Assertions.assertNotNull(service.getAllMovies());
        Assertions.assertEquals(1, service.getAllMovies().size());

        //validate mapping
        Assertions.assertEquals("abc", service.getAllMovies().get(0).getTitle());
        Assertions.assertEquals(Duration.ofMinutes(60), service.getAllMovies().get(0).getRunningTime());
        Assertions.assertEquals(10.0, service.getAllMovies().get(0).getTicketPrice());
        Assertions.assertEquals(1, service.getAllMovies().get(0).getSpecialCode());
    }

    /**
     * Test to validate a scenario when given movie already present in the theater (or in the list)
     */
    @Test
    public void addNewMovie_MovieAlreadyPresent() {
        TheaterManagementService service = new TheaterManagementService();
        Optional<Movie> m1 = service.addNewMovie("abc", Duration.ofMinutes(60), 10.0, 1);
        Optional<Movie> m2 = service.addNewMovie("abc", Duration.ofMinutes(60), 7.50, 0);

        Assertions.assertTrue(m1.isPresent());
        Assertions.assertFalse(m2.isPresent());
        Assertions.assertNotNull(service.getAllMovies());
        Assertions.assertEquals(1, service.getAllMovies().size());
    }

    /**
     * Test to validate a scenario when removing existing movie from the theater is successfull
     */
    @Test
    public void removeMovie_happyPath() {
        TheaterManagementService service = new TheaterManagementService();
        Optional<Movie> m1 = service.addNewMovie("abc", Duration.ofMinutes(60), 10.0, 1);
        Optional<Movie> m2 = service.addNewMovie("xyz", Duration.ofMinutes(30), 7.50, 0);

        ServiceResponse actualRes = service.removeMovie(m2.get().getMovieId());

        Assertions.assertTrue(actualRes.isSuccess());
        Assertions.assertEquals(1, service.getAllMovies().size());
        Assertions.assertEquals("abc", service.getAllMovies().get(0).getTitle());
    }

    /**
     * Test to validate a scenario when given movie to remove is not present in the theater (or in the list)
     */
    @Test
    public void removeMovie_MovieNotPresent() {
        TheaterManagementService service = new TheaterManagementService();
        Optional<Movie> m1 = service.addNewMovie("abc", Duration.ofMinutes(60), 10.0, 1);

        ServiceResponse actualRes = service.removeMovie(m1.get().getMovieId() + 1);

        Assertions.assertFalse(actualRes.isSuccess());
        Assertions.assertEquals(1, service.getAllMovies().size());
    }

    /**
     * Test to validate a scenario when retrieving all movies currently present in the theater is successfull
     */
    @Test
    public void getAllMovies_SuccessfulRetrieval() {
        TheaterManagementService service = new TheaterManagementService();
        Optional<Movie> m1 = service.addNewMovie("abc", Duration.ofMinutes(60), 10.0, 1);
        Optional<Movie> m2 = service.addNewMovie("xyz", Duration.ofMinutes(30), 7.50, 0);

        List<Movie> actualRes = service.getAllMovies();

        Assertions.assertNotNull(actualRes);
        Assertions.assertFalse(actualRes.isEmpty());
        Assertions.assertEquals(2, actualRes.size());
    }

    /**
     * Test to validate a scenario when adding a new show in the theater is successfull
     */
    @Test
    public void addNewShow_happyPath() throws TheaterServiceException {
        TheaterManagementService service = new TheaterManagementService();
        Screen screen = service.addNewScreen(10);
        Optional<Movie> m1 = service.addNewMovie("abc", Duration.ofMinutes(60), 10.0, 1);

        Optional<Showing> actualRes = service
                .addNewShow(screen, m1.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(9, 0)));
        List<Showing> allShows =  service.getAllShowsForGivenScreen(screen.getScreenId());

        Assertions.assertTrue(actualRes.isPresent());
        Assertions.assertEquals(1, allShows.size());
        Assertions.assertEquals(1, allShows.get(0).getSequenceOfTheDay());
    }

    /**
     * Test to validate a scenario when given new show's start time conflict with existing show's time interval
     */
    @Test
    public void addNewShow_ConflictWithExistingShow_StartTimeConflict() throws TheaterServiceException {
        TheaterManagementService service = new TheaterManagementService();
        Screen screen = service.addNewScreen(10);
        Optional<Movie> m1 = service.addNewMovie("abc", Duration.ofMinutes(90), 10.0, 1);
        Optional<Movie> m2 = service.addNewMovie("xyz", Duration.ofMinutes(60), 10.0, 1);
        Optional<Movie> m3 = service.addNewMovie("pqr", Duration.ofMinutes(90), 10.0, 1);
        Optional<Showing> s1 = service
                .addNewShow(screen, m1.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(9, 0)));
        Optional<Showing> s2 = service
                .addNewShow(screen, m2.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(10, 0)));
        Optional<Showing> s3 = service
                .addNewShow(screen, m3.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(12, 0)));

        List<Showing> allShows =  service.getAllShowsForGivenScreen(screen.getScreenId());

        Assertions.assertTrue(s1.isPresent());
        Assertions.assertFalse(s2.isPresent()); //conflicted show
        Assertions.assertTrue(s3.isPresent());

        Assertions.assertEquals(2, allShows.size());
        Assertions.assertEquals("abc", allShows.get(0).getMovie().getTitle());
        Assertions.assertEquals(1, allShows.get(0).getSequenceOfTheDay());
        Assertions.assertEquals("pqr", allShows.get(1).getMovie().getTitle());
        Assertions.assertEquals(2, allShows.get(1).getSequenceOfTheDay());
    }

    /**
     * Test to validate a scenario when given new show's end time conflict with existing show's time interval
     */
    @Test
    public void addNewShow_ConflictWithExistingShow_EndTimeConflict() throws TheaterServiceException {
        TheaterManagementService service = new TheaterManagementService();
        Screen screen = service.addNewScreen(10);
        Optional<Movie> m1 = service.addNewMovie("abc", Duration.ofMinutes(90), 10.0, 1);
        Optional<Movie> m2 = service.addNewMovie("xyz", Duration.ofMinutes(60), 10.0, 1);
        Optional<Movie> m3 = service.addNewMovie("pqr", Duration.ofMinutes(90), 10.0, 1);
        Optional<Showing> s1 = service
                .addNewShow(screen, m1.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(9, 0)));
        Optional<Showing> s2 = service
                .addNewShow(screen, m2.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(12, 0)));
        Optional<Showing> s3 = service
                .addNewShow(screen, m3.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(11, 30)));

        List<Showing> allShows =  service.getAllShowsForGivenScreen(screen.getScreenId());

        Assertions.assertTrue(s1.isPresent());
        Assertions.assertTrue(s2.isPresent());
        Assertions.assertFalse(s3.isPresent()); //conflicted show

        Assertions.assertEquals(2, allShows.size());
        Assertions.assertEquals("abc", allShows.get(0).getMovie().getTitle());
        Assertions.assertEquals(1, allShows.get(0).getSequenceOfTheDay());
        Assertions.assertEquals("xyz", allShows.get(1).getMovie().getTitle());
        Assertions.assertEquals(2, allShows.get(1).getSequenceOfTheDay());
    }

    /**
     * Test to validate a scenario when given new show needs to be squeezed in the middle of list of all schedules.
     * In this case, after successfull adding of new show, the sequence of all shows starting after that newly
     * added show is expected to be updated/shifted by one
     */
    @Test
    public void addNewShow_UpdateSequenceAfterAdding() throws TheaterServiceException {
        TheaterManagementService service = new TheaterManagementService();
        Screen screen = service.addNewScreen(10);
        Optional<Movie> m1 = service.addNewMovie("abc", Duration.ofMinutes(60), 10.0, 1);
        Optional<Movie> m2 = service.addNewMovie("xyz", Duration.ofMinutes(90), 10.0, 1);
        Optional<Movie> m3 = service.addNewMovie("pqr", Duration.ofMinutes(90), 10.0, 1);
        Optional<Showing> s1 = service
                .addNewShow(screen, m1.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(9, 0)));
        Optional<Showing> s2 = service
                .addNewShow(screen, m2.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(11, 30)));
        Optional<Showing> s3 = service
                .addNewShow(screen, m3.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(10, 0)));

        List<Showing> allShows =  service.getAllShowsForGivenScreen(screen.getScreenId());

        Assertions.assertTrue(s1.isPresent());
        Assertions.assertTrue(s2.isPresent());
        Assertions.assertTrue(s3.isPresent());

        Assertions.assertEquals(3, allShows.size());
        Assertions.assertEquals("abc", allShows.get(0).getMovie().getTitle());
        Assertions.assertEquals(1, allShows.get(0).getSequenceOfTheDay());
        Assertions.assertEquals("pqr", allShows.get(1).getMovie().getTitle());
        Assertions.assertEquals(2, allShows.get(1).getSequenceOfTheDay());
        Assertions.assertEquals("xyz", allShows.get(2).getMovie().getTitle()); //this show got shifted by one sequence
        Assertions.assertEquals(3, allShows.get(2).getSequenceOfTheDay());
    }

    /**
     * Test to validate a scenario when invalid movie is provided while adding a show
     */
    @Test
    public void addNewShow_InvalidMovie_ThrowsException() {
        TheaterManagementService service = new TheaterManagementService();
        Screen screen = service.addNewScreen(10);
        String errorMsg = "";
        try {
            Optional<Movie> m1 = service.addNewMovie("abc", Duration.ofMinutes(60), 10.0, 1);
            Optional<Movie> m2 = service.addNewMovie("xyz", Duration.ofMinutes(90), 10.0, 1);

            service.addNewShow(screen, new Movie(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(10, 0)));
        } catch (TheaterServiceException e) {
            errorMsg = e.getMessage();
        }

        Assertions.assertEquals(0, service.getAllShows().get(screen.getScreenId()).size());
        Assertions.assertTrue(errorMsg.contains(NO_MOVIES));
    }

    /**
     * Test to validate a scenario when invalid screen is provided while adding a show
     */
    @Test
    public void addNewShow_InvalidScreen_ThrowsException() {
        TheaterManagementService service = new TheaterManagementService();
        Screen screen = service.addNewScreen(10);
        String errorMsg = "";
        try {
            Optional<Movie> m1 = service.addNewMovie("abc", Duration.ofMinutes(60), 10.0, 1);
            Optional<Movie> m2 = service.addNewMovie("xyz", Duration.ofMinutes(90), 10.0, 1);

            service.addNewShow(new Screen(), m1.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(10, 0)));
        } catch (TheaterServiceException e) {
            errorMsg = e.getMessage();
        }

        Assertions.assertEquals(0, service.getAllShows().get(screen.getScreenId()).size());
        Assertions.assertTrue(errorMsg.contains(NO_SCREEN));
    }

    /**
     * Test to validate a scenario when given show is successfully removed from the theater from all screens
     */
    @Test
    public void removeAllShowsForGivenMovie_happyPath() throws TheaterServiceException {
        TheaterManagementService service = new TheaterManagementService();
        Screen screen = service.addNewScreen(10);
        Optional<Movie> m1 = service.addNewMovie("abc", Duration.ofMinutes(60), 10.0, 1);
        Optional<Movie> m2 = service.addNewMovie("xyz", Duration.ofMinutes(90), 10.0, 1);

        Optional<Showing> s1 = service
                .addNewShow(screen, m1.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(9, 0)));
        Optional<Showing> s2 = service
                .addNewShow(screen, m2.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(11, 30)));
        Optional<Showing> s3 = service
                .addNewShow(screen, m1.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(10, 0)));

        ServiceResponse actualRes = service.removeAllShowsForGivenMovie(m1.get());

        Assertions.assertTrue(actualRes.isSuccess());
        Assertions.assertEquals(1, service.getAllShowsForGivenScreen(screen.getScreenId()).size());
    }

    /**
     * Test to validate a scenario when given show is successfully removed from the theater from given screen only
     */
    @Test
    public void removeAllShowsForGivenMovieAndScreen_happyPath() throws TheaterServiceException {
        TheaterManagementService service = new TheaterManagementService();
        Screen sc1 = service.addNewScreen(10);
        Screen sc2 = service.addNewScreen(5);
        Optional<Movie> m1 = service.addNewMovie("abc", Duration.ofMinutes(60), 10.0, 1);
        Optional<Movie> m2 = service.addNewMovie("xyz", Duration.ofMinutes(90), 10.0, 1);

        Optional<Showing> s1 = service
                .addNewShow(sc1, m1.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(9, 0)));
        Optional<Showing> s2 = service
                .addNewShow(sc1, m2.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(11, 30)));
        Optional<Showing> s3 = service
                .addNewShow(sc2, m1.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(10, 0)));
        Optional<Showing> s4 = service
                .addNewShow(sc2, m2.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(12, 0)));

        ServiceResponse actualRes = service.removeAllShowsForGivenMovieAndScreen(m1.get(), sc2.getScreenId());

        Assertions.assertTrue(actualRes.isSuccess());
        Assertions.assertEquals(2, service.getAllShowsForGivenScreen(sc1.getScreenId()).size());
        Assertions.assertEquals(1, service.getAllShowsForGivenScreen(sc2.getScreenId()).size());
    }

    /**
     * Test to validate a scenario when retrieving all shows from all screens is successful
     */
    @Test
    public void getAllShows_SuccessfulRetrieval() throws TheaterServiceException {
        TheaterManagementService service = new TheaterManagementService();
        Screen sc1 = service.addNewScreen(10);
        Screen sc2 = service.addNewScreen(5);
        Optional<Movie> m1 = service.addNewMovie("abc", Duration.ofMinutes(60), 10.0, 1);
        Optional<Movie> m2 = service.addNewMovie("xyz", Duration.ofMinutes(90), 10.0, 1);

        Optional<Showing> s1 = service
                .addNewShow(sc1, m1.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(9, 0)));
        Optional<Showing> s2 = service
                .addNewShow(sc1, m2.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(11, 30)));
        Optional<Showing> s3 = service
                .addNewShow(sc2, m1.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(10, 0)));
        Optional<Showing> s4 = service
                .addNewShow(sc2, m2.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(12, 0)));

        Map<Integer, List<Showing>> actualRes = service.getAllShows();

        Assertions.assertNotNull(actualRes);
        Assertions.assertEquals(2, actualRes.size());
        Assertions.assertEquals(2, actualRes.get(sc1.getScreenId()).size());
        Assertions.assertEquals(2, actualRes.get(sc2.getScreenId()).size());
    }

    /**
     * Test to validate a scenario when retrieving all shows from given screen is successful
     */
    @Test
    public void getAllShowsForGivenScreen_SuccessfulRetrieval() throws TheaterServiceException {
        TheaterManagementService service = new TheaterManagementService();
        Screen sc1 = service.addNewScreen(10);
        Screen sc2 = service.addNewScreen(5);
        Optional<Movie> m1 = service.addNewMovie("abc", Duration.ofMinutes(60), 10.0, 1);
        Optional<Movie> m2 = service.addNewMovie("xyz", Duration.ofMinutes(90), 10.0, 1);

        Optional<Showing> s1 = service
                .addNewShow(sc1, m1.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(9, 0)));
        Optional<Showing> s2 = service
                .addNewShow(sc1, m2.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(11, 30)));
        Optional<Showing> s3 = service
                .addNewShow(sc2, m1.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(10, 0)));
        Optional<Showing> s4 = service
                .addNewShow(sc2, m2.get(), LocalDateTime.of(LocalDateProvider.currentDate(), LocalTime.of(12, 0)));

        List<Showing> actualRes = service.getAllShowsForGivenScreen(sc1.getScreenId());

        Assertions.assertNotNull(actualRes);
        Assertions.assertEquals(2, actualRes.size());
    }

    /**
     * Test to validate a scenario when adding a new screen in the theater is successful
     */
    @Test
    public void addNewScreen_happyPath() {
        TheaterManagementService service = new TheaterManagementService();
        Screen sc1 = service.addNewScreen(10);

        Assertions.assertEquals(1, service.getAllScreens().size());
    }

    /**
     * Test to validate a scenario when removing given screen from the theater is successful
     */
    @Test
    public void removeScreen_happyPath() {
        TheaterManagementService service = new TheaterManagementService();
        Screen sc1 = service.addNewScreen(10);
        Screen sc2 = service.addNewScreen(5);

        ServiceResponse actualRes = service.removeScreen(sc1.getScreenId());

        Assertions.assertTrue(actualRes.isSuccess());
        Assertions.assertEquals(1, service.getAllScreens().size());

        //validating all shows also get deleted when a screen is removed
        Assertions.assertNull(service.getAllShows().get(sc1.getScreenId()));
    }

    /**
     * Test to validate a scenario when trying to remove a screen which is not present in the theater
     */
    @Test
    public void removeScreen_ScreenNotPresent() {
        TheaterManagementService service = new TheaterManagementService();
        Screen sc1 = service.addNewScreen(10);

        ServiceResponse actualRes = service.removeScreen(sc1.getScreenId() + 1);

        Assertions.assertFalse(actualRes.isSuccess());
        Assertions.assertEquals(1, service.getAllScreens().size());
    }
}
