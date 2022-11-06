package com.jpmc.theater.service;

import com.jpmc.theater.model.Movie;
import com.jpmc.theater.model.Screen;
import com.jpmc.theater.model.Showing;
import com.jpmc.theater.util.LocalDateProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DiscountServiceTest {

    /**
     * Test to validate discount rate for special movie
     */
    @Test
    public void calculateDiscount_SpecialMovieDiscount() {
        DiscountService service = new DiscountService();
        Screen s1 = new Screen(5);
        Movie m1 = new Movie("abc", Duration.ofMinutes(60), 10.0, 1);
        Showing show = new Showing(m1, s1, 3,
                LocalDateTime.of(LocalDate.of(2022, 11, 05), LocalTime.of(9, 0)));

        double actualDiscount = service.calculateDiscount(show);
        Assertions.assertEquals(2.0, actualDiscount);
    }

    /**
     * Test to validate discount rate for movie showing 1st of the day
     */
    @Test
    public void calculateDiscount_SequenceBasedDiscount_Sequence1() {
        DiscountService service = new DiscountService();
        Screen s1 = new Screen(5);
        Movie m1 = new Movie("abc", Duration.ofMinutes(60), 10.0, 0);
        Showing show = new Showing(m1, s1, 1,
                LocalDateTime.of(LocalDate.of(2022, 11, 05), LocalTime.of(9, 0)));

        double actualDiscount = service.calculateDiscount(show);
        Assertions.assertEquals(3.0, actualDiscount);
    }

    /**
     * Test to validate discount rate for movie showing 2nd of the day
     */
    @Test
    public void calculateDiscount_SequenceBasedDiscount_Sequence2() {
        DiscountService service = new DiscountService();
        Screen s1 = new Screen(5);
        Movie m1 = new Movie("abc", Duration.ofMinutes(60), 10.0, 0);
        Showing show = new Showing(m1, s1, 2,
                LocalDateTime.of(LocalDate.of(2022, 11, 05), LocalTime.of(9, 0)));

        double actualDiscount = service.calculateDiscount(show);
        Assertions.assertEquals(2.0, actualDiscount);
    }

    /**
     * Test to validate discount rate for shows between 11 am and 4 pm
     */
    @Test
    public void calculateDiscount_TimingDiscount() {
        DiscountService service = new DiscountService();
        Screen s1 = new Screen(5);
        Movie m1 = new Movie("abc", Duration.ofMinutes(60), 10.0, 0);
        Showing show = new Showing(m1, s1, 4,
                LocalDateTime.of(LocalDate.of(2022, 11, 05), LocalTime.of(12, 0)));

        double actualDiscount = service.calculateDiscount(show);
        Assertions.assertEquals(2.5, actualDiscount);
    }

    /**
     * Test to validate discount rate for shows on 7th day of every month
     */
    @Test
    public void calculateDiscount_SpecificDayDiscount() {
        DiscountService service = new DiscountService();
        Screen s1 = new Screen(5);
        Movie m1 = new Movie("abc", Duration.ofMinutes(60), 10.0, 0);
        Showing show = new Showing(m1, s1, 3,
                LocalDateTime.of(LocalDate.of(2022, 11, 07), LocalTime.of(9, 0)));

        double actualDiscount = service.calculateDiscount(show);
        Assertions.assertEquals(1.0, actualDiscount);
    }

    /**
     * Test to validate scenario when multiple discount rules can be applied for given reservation
     */
    @Test
    public void calculateDiscount_MultipleRulesApply() {
        DiscountService service = new DiscountService();
        Screen s1 = new Screen(5);
        Movie m1 = new Movie("abc", Duration.ofMinutes(60), 10.0, 1);
        Showing show = new Showing(m1, s1, 1,
                LocalDateTime.of(LocalDate.of(2022, 11, 05), LocalTime.of(9, 0)));

        //Two discount rules will apply - special movie discount and sequence discount. Sequence discount will win
        double actualDiscount = service.calculateDiscount(show);
        Assertions.assertEquals(3.0, actualDiscount);
    }

    /**
     * Test to validate discount rate when no rules apply
     */
    @Test
    public void calculateDiscount_NoDiscount() {
        DiscountService service = new DiscountService();
        Screen s1 = new Screen(5);
        Movie m1 = new Movie("abc", Duration.ofMinutes(60), 10.0, 0);
        Showing show = new Showing(m1, s1, 3,
                LocalDateTime.of(LocalDate.of(2022, 11, 05), LocalTime.of(9, 0)));

        double actualDiscount = service.calculateDiscount(show);
        Assertions.assertEquals(0.0, actualDiscount);
    }
}
