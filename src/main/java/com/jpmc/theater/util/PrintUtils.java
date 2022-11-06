package com.jpmc.theater.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PrintUtils {

    public static ObjectMapper mapper = new ObjectMapper()
            .findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static void printSimpleFormat(List<? extends Object> objects, String objectName) {
        System.out.println(objectName + " -- " + LocalDateProvider.currentDate());
        System.out.println("===================================================");
        objects.forEach(s -> System.out.println(s.toString()));
        System.out.println("===================================================");
    }

    public static void printJsonFormat(List<? extends Object> objects, String objectName) {
        try {
            System.out.println(objectName + " (JSON Format) -- " + LocalDateProvider.currentDate());
            System.out.println("===================================================");
            String jsonString = mapper.writeValueAsString(objects);
            System.out.println(jsonString);
            System.out.println("===================================================");
        } catch (JsonProcessingException e) {
            System.out.println("Printing JSON format failed - " + e.getMessage());
        }
    }

    public static String humanReadableFormat(Duration duration) {
        long hour = duration.toHours();
        long remainingMin = duration.toMinutes() - TimeUnit.HOURS.toMinutes(duration.toHours());

        return String.format("(%s hour%s %s minute%s)", hour, handlePlural(hour), remainingMin, handlePlural(remainingMin));
    }

    // (s) postfix should be added to handle plural correctly
    private static String handlePlural(long value) {
        if (value == 1) {
            return "";
        } else {
            return "s";
        }
    }
}
