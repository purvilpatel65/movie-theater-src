package com.jpmc.theater.service;

import com.jpmc.theater.exception.TheaterServiceException;
import com.jpmc.theater.model.Screen;
import com.jpmc.theater.model.ServiceResponse;
import com.jpmc.theater.model.Showing;
import com.jpmc.theater.model.Movie;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.jpmc.theater.util.Constants.*;

public class TheaterManagementService {

    private List<Movie> movies;
    private List<Screen> screens;
    private Map<Integer, List<Showing>> shows;

    public TheaterManagementService() {
        movies = new ArrayList<>();
        screens = new ArrayList<>();
        shows = new HashMap<>();
    }

    /**
     * This method add new movie into the theater
     *
     * @param  title  title of the movie
     * @param  runningTime duration of the movie
     * @param  ticketPrice price of a ticket for given movie
     * @param  specialCode special movie code
     * @return optional reference to newly added movie object
     */
    public Optional<Movie> addNewMovie(String title, Duration runningTime, double ticketPrice, int specialCode) {
        Movie newMovie = null;
        boolean isAlreadyPresent = movies.stream()
                .anyMatch(m -> m.getTitle().equalsIgnoreCase(title));

        if (!isAlreadyPresent) {
            newMovie = new Movie(title, runningTime, ticketPrice, specialCode);
            movies.add(newMovie);
        }
        return Optional.ofNullable(newMovie);
    }

    /**
     * This method removes an existing movie from the theater
     *
     * @param  movieId  id of the movie to be removed
     * @return service response containing operation's success and its related message
     */
    public ServiceResponse removeMovie(int movieId) {

        Optional<Movie> movie = movies.stream()
                .filter(m -> m.getMovieId() == movieId)
                .findAny();

        if (!movie.isPresent()) return new ServiceResponse(false, NO_MOVIES);

        this.removeAllShowsForGivenMovie(movie.get());
        movies.remove(movie.get());
        return new ServiceResponse(true, MOVIE_REMOVED);
    }

    /**
     * This method returns all movies currently present in the theater
     *
     * @return list of all movies currently present in the theater
     */
    public List<Movie> getAllMovies() {
        return movies;
    }

    /**
     * This method add new show into the theater for a given movie.
     * It calculated sequence of newly added show by performing time comparison with existing shows.
     * It also validates timing conflicts with existing shows. If there is a conflict, then it will not
     * add given new show and return empty optional.
     *
     * @param  screen  screen object reference where new show is expect to be added
     * @param  movie movie object reference associated with the show
     * @param  anticipatedStartTime start date and time of the show
     * @return optional reference to newly added show object
     */
    public Optional<Showing> addNewShow(Screen screen, Movie movie, LocalDateTime anticipatedStartTime)
            throws TheaterServiceException {

        if (!screens.contains(screen)) throw new TheaterServiceException(NO_SCREEN + " - " + screen.getScreenId());
        if (!movies.contains(movie)) throw new TheaterServiceException(NO_MOVIES + " - " + movie.getTitle());

        Showing newShow = null;
        List<Showing> currentShowList = shows.get(screen.getScreenId());

        Predicate<Showing> predicate =
                s -> s.getStartTime().toLocalDate().compareTo(anticipatedStartTime.toLocalDate()) == 0;
        List<Showing> allShowsForGivenDate = currentShowList.stream()
                .filter(predicate)
                .sorted(Comparator.comparing(Showing::getSequenceOfTheDay))
                .collect(Collectors.toList());

        int seq;
        for (seq = 0; seq < allShowsForGivenDate.size(); ++seq) {
            Showing currShow = allShowsForGivenDate.get(seq);

            LocalDateTime currShowStartTime = currShow.getStartTime();
            LocalDateTime currShowEndTime = currShowStartTime
                    .plusMinutes(currShow.getMovie().getRunningTime().toMinutes());

            LocalDateTime newShowEndTime = anticipatedStartTime
                    .plusMinutes(movie.getRunningTime().toMinutes());

            if (anticipatedStartTime.isEqual(currShowEndTime)
                    || anticipatedStartTime.isAfter(currShowEndTime)) continue;
            if (isFallBetweenInterval(currShowStartTime, currShowEndTime, anticipatedStartTime, newShowEndTime)) {
                return Optional.empty();
            }
            break;
        }

        newShow = new Showing(movie, screen, seq + 1, anticipatedStartTime);
        moveSequenceByOne(allShowsForGivenDate, seq);
        currentShowList.add(newShow);

        return Optional.ofNullable(newShow);
    }

    /**
     * This method removes all existing shows for a given movie from the theater
     *
     * @param  movie movie object reference associated with the show
     * @return service response containing operation's success and its related message
     */
    public ServiceResponse removeAllShowsForGivenMovie(Movie movie) {
        shows.entrySet().forEach(screenShows -> {
            Predicate<Showing> predicate = s -> s.getMovie().getMovieId() == movie.getMovieId();
            screenShows.getValue().removeIf(predicate);
        });
        return new ServiceResponse(true, SHOW_CANCELLED_SUCCESS);
    }

    /**
     * This method removes all existing shows for a given movie from a given screen from the theater
     *
     * @param  movie movie object reference associated with the show
     * @param  screenId id of the screen from where movies are expected to be removed
     * @return service response containing operation's success and its related message
     */
    public ServiceResponse removeAllShowsForGivenMovieAndScreen(Movie movie, int screenId) {
        List<Showing> screenShows = shows.get(screenId);

        if (screenShows == null) return new ServiceResponse(false, NO_SCREEN);

        Predicate<Showing> predicate = s -> s.getMovie().getMovieId() == movie.getMovieId();
        screenShows.removeIf(predicate);
        return new ServiceResponse(true, SHOW_CANCELLED_SUCCESS);
    }

    /**
     * This method returns all shows (from all screens) currently present in the theater
     *
     * @return map of all screens and their respective list of shows currently present in the theater
     */
    public Map<Integer, List<Showing>> getAllShows() {
        return this.shows;
    }

    public List<Showing> getAllShowsForGivenScreen(int screenId) {
        return shows.get(screenId).stream()
                .sorted(Comparator.comparing(Showing::getSequenceOfTheDay))
                .collect(Collectors.toList());
    }

    /**
     * This method add a new screen in the theater
     *
     * @param  totalSeats total number of seats for given screen
     * @return newly added screen object reference
     */
    public Screen addNewScreen(int totalSeats) {
        Screen newScreen = new Screen(totalSeats);
        screens.add(newScreen);

        List<Showing> screenShows = new ArrayList<>();
        shows.put(newScreen.getScreenId(), screenShows);
        return newScreen;
    }

    /**
     * This method removes existing screen in the theater
     *
     * @param  screenId id of the screen to be removed
     * @return service response containing operation's success and its related message
     */
    public ServiceResponse removeScreen(int screenId) {
        Optional<Screen> screen = screens.stream()
                .filter(s -> s.getScreenId() == screenId)
                .findAny();

        if (!screen.isPresent()) return new ServiceResponse(false, NO_SCREEN);

        screens.remove(screen.get());
        shows.remove(screenId);
        return new ServiceResponse(true, SCREEN_REMOVED);
    }

    /**
     * This method returns all existing screens in the theater
     *
     * @return list of all existing screen in the theater
     */
    public List<Screen> getAllScreens() {
        return screens;
    }

    /**
     * This helper method will help in validating timing conflicts while adding a new show
     *
     * @param  s1 start time of existing show
     * @param  s2 end time of existing show
     * @param  m1 start time of new show
     * @param  m2 end time of new show
     * @return boolean representing whether new show's time interval conflicts with existing show's time interval
     */
    private boolean isFallBetweenInterval(LocalDateTime s1, LocalDateTime s2, LocalDateTime m1, LocalDateTime m2) {
        return (s1.isEqual(m1) || s2.isEqual(m2)) ||
                ((m1.isAfter(s1) && m1.isBefore(s2)) || (m2.isAfter(s1) && m2.isBefore(s2)));
    }

    /**
     * This helper method will help in adjusting the sequence of existing shows (if required) when a new show is added
     *
     * @param  shows list of all shows whose sequence needs to be adjusted
     * @param  startSeq starting sequence of all show from given list
     * @return void
     */
    private void moveSequenceByOne(List<Showing> shows, int startSeq) {
        for (int i = startSeq; i < shows.size(); i++) {
            int currSeq = shows.get(i).getSequenceOfTheDay();
            shows.get(i).setSequenceOfTheDay(currSeq + 1);
        }
    }
}
