package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtils {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDate parseDate(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr, formatter);
            if (date.isBefore(LocalDate.now())) {
                System.out.println("❌ Date must be in the future.");
                return null;
            }
            return date;
        } catch (DateTimeParseException e) {
            System.out.println("❌ Invalid date format. Please use yyyy-MM-dd.");
            return null;
        }
    }

    public static boolean isValidDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) return false;

        if (start.isAfter(end)) {
            System.out.println("❌ Start date must be before end date.");
            return false;
        }

        return true;
    }

    public static long daysBetween(LocalDate start, LocalDate end) {
        return java.time.temporal.ChronoUnit.DAYS.between(start, end);
    }
}
