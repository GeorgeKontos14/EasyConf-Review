package nl.tudelft.sem.template.example.domain.util;

import java.time.LocalDate;

public class DateUtils {

    /**
     * Method that advances the date by one week.
     *
     * @param currentDate the current date.
     * @return the new date
     */
    public static String advanceOneWeek(String currentDate) {
        LocalDate currentDateObject = LocalDate.parse(currentDate);
        LocalDate advanced = currentDateObject.plusWeeks(1);
        return advanced.toString();
    }
}
