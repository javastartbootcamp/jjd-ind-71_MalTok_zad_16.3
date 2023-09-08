package pl.javastart.task;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static final String DISPLAY_PATTERN = "yyyy-MM-dd HH:mm:ss";
    static final Format[] PATTERNS = new Format[]{
        new Format("yyyy-MM-dd HH:mm:ss", false),
        new Format("yyyy-MM-dd", true),
        new Format("dd.MM.yyyy HH:mm:ss", false)
    };

    public static void main(String[] args) {
        Main main = new Main();
        main.run(new Scanner(System.in));
    }

    public void run(Scanner scanner) {
        // uzupełnij rozwiązanie. Korzystaj z przekazanego w parametrze scannera
        ZonedDateTime defaultDateTime = getDateTime(scanner);
        showTimeAtDifferentZones(defaultDateTime);
    }

    private void showAvailableFormats() {
        System.out.println("Dostępne formaty danych:");
        for (Format format : PATTERNS) {
            System.out.println(format);
        }
    }

    private ZonedDateTime getDateTime(Scanner scanner) {
        showAvailableFormats();
        System.out.println("Podaj datę:");
        String userInput = scanner.nextLine();

        LocalDateTime localDateTime = null;
        ZonedDateTime zonedDateTime = null;

        if (userInput.startsWith("t")) {
            try {
                localDateTime = getDateTimeFromCurrentDateTime(userInput);
            } catch (IllegalArgumentException e) {
                //ignore
            }
        } else {
            for (Format format : PATTERNS) {
                try {
                    DateTimeFormatter pattern = DateTimeFormatter.ofPattern(format.getFormat());
                    TemporalAccessor temporalAccessor = pattern.parse(userInput);

                    if (format.isOnlyDate()) {
                        LocalDate date = LocalDate.from(temporalAccessor);
                        localDateTime = date.atTime(LocalTime.MIDNIGHT);

                    } else {
                        localDateTime = LocalDateTime.from(temporalAccessor);
                    }

                } catch (DateTimeParseException e) {
                    //ignore
                }
            }
        }
        if (localDateTime == null) {
            System.out.println("Nie udało się utworzyć daty");
        } else {
            zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        }
        return zonedDateTime;
    }

    private LocalDateTime getDateTimeFromCurrentDateTime(String input) {
        LocalDateTime t = LocalDateTime.now();
        LocalDateTime resultDateTime;
        Pattern pattern = Pattern.compile("t([+-]?\\d*y)?([+-]?\\d*M)?([+-]?\\d*d)?([+-]?\\d*h)?([+-]?\\d*m)?([+-]?\\d*s)?");
        Matcher matcher = pattern.matcher(input);

        if (matcher.matches()) {
            long years = replaceCharacter(matcher.group(1), "y");
            long months = replaceCharacter(matcher.group(2), "M");
            long days = replaceCharacter(matcher.group(3), "d");
            long hours = replaceCharacter(matcher.group(4), "h");
            long minutes = replaceCharacter(matcher.group(5), "m");
            long seconds = replaceCharacter(matcher.group(6), "s");

            resultDateTime = t
                    .plusYears(years)
                    .plusMonths(months)
                    .plusDays(days)
                    .plusHours(hours)
                    .plusMinutes(minutes)
                    .plusSeconds(seconds);
        } else {
            throw new IllegalArgumentException("Invalid pattern: " + pattern);
        }
        return resultDateTime;
    }

    private long replaceCharacter(String input, String charToReplace) {
        if (input == null || input.isEmpty()) {
            return 0;
        }
        input = input.replace(charToReplace, "".trim());
        return Long.parseLong(input);
    }

    private void showTimeAtDifferentZones(ZonedDateTime zonedDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DISPLAY_PATTERN);
        for (Zone zone : Zone.values()) {
            ZonedDateTime result = zonedDateTime.withZoneSameInstant(zone.getZoneId());
            System.out.println(zone.getDisplayName() + ": " + result.format(formatter));
        }
    }
}
