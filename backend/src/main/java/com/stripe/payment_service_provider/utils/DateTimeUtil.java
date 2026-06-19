package com.stripe.payment_service_provider.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static LocalDate convertInstantToLocalDate(Instant instant) {
        return instant.atZone(ZoneOffset.UTC).toLocalDate();
    }

    public static DateTimeFormatter getDateTimeFormatter(String pattern, ZoneId zoneId) {
        String PATTERN_FORMAT = "dd/MM/yyyy";
        return DateTimeFormatter
                .ofPattern(pattern != null ? pattern : PATTERN_FORMAT).withZone(zoneId);
    }

    public static LocalDate getStartOfCurrentYearUtc() {
        return LocalDate.now(ZoneOffset.UTC).withDayOfYear(1);
    }
}
