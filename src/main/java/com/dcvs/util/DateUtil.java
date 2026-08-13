package com.dcvs.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Date formatting and expiry calculation helpers.
 * Module 6 — Lakshmi
 */
public final class DateUtil {

    public static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");
    public static final DateTimeFormatter ISO_FORMAT =
            DateTimeFormatter.ISO_LOCAL_DATE;

    private DateUtil() {}

    /** Formats a LocalDate as "dd MMM yyyy" for display (e.g. 15 Mar 2026). */
    public static String format(LocalDate date) {
        return date == null ? "N/A" : date.format(DISPLAY_FORMAT);
    }

    /** Returns true if the expiry date is in the past. */
    public static boolean isExpired(LocalDate expiryDate) {
        return expiryDate != null && LocalDate.now().isAfter(expiryDate);
    }

    /** Returns the default expiry date: issue date + 2 years. */
    public static LocalDate defaultExpiry(LocalDate issueDate) {
        return issueDate.plusYears(2);
    }

    /** Parses a yyyy-MM-dd string safely, returns null on failure. */
    public static LocalDate parse(String dateStr) {
        try {
            return LocalDate.parse(dateStr, ISO_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }
}
