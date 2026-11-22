package bioflix.catalog.service;

import bioflix.catalog.entity.Hall;
import bioflix.catalog.entity.SeatType;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class CatalogValidation {

    public static final int SLUG_MAX = 80;
    public static final int DURATION_MIN = 30;
    public static final int DURATION_MAX = 300;
    public static final int AD_MINUTES = 25;
    private static final Pattern SLUG = Pattern.compile("^[a-z0-9]+(-[a-z0-9]+)*$");

    public List<String> validateSlug(String slug) {
        List<String> errors = new ArrayList<>();
        if (slug == null || slug.isEmpty()) {
            errors.add("Slug is required");
            return errors;
        }
        if (slug.length() > SLUG_MAX) {
            errors.add("Slug must be at most " + SLUG_MAX + " characters");
        }
        if (!SLUG.matcher(slug).matches()) {
            errors.add("Slug may only contain lowercase letters, digits and single hyphens between parts");
        }
        return errors;
    }

    public List<String> validateDuration(int minutes) {
        List<String> errors = new ArrayList<>();
        if (minutes < DURATION_MIN || minutes > DURATION_MAX) {
            errors.add("Duration must be between " + DURATION_MIN + " and " + DURATION_MAX + " minutes");
        }
        return errors;
    }

    /** Every seat type the hall contains needs a positive price. */
    public List<String> validatePrices(Map<SeatType, Integer> prices, Set<SeatType> requiredTypes) {
        List<String> errors = new ArrayList<>();
        if (prices == null || prices.isEmpty()) {
            errors.add("Prices are required");
            return errors;
        }
        for (SeatType type : requiredTypes) {
            if (!prices.containsKey(type)) {
                errors.add("Missing price for " + type);
            }
        }
        prices.forEach((type, price) -> {
            if (price == null || price <= 0) {
                errors.add("Price for " + type + " must be positive");
            }
        });
        return errors;
    }

    /** Seat positions (row label + seat number) must be unique inside a hall. */
    public List<String> validateSeatLayout(List<Hall.SeatRow> rows) {
        List<String> errors = new ArrayList<>();
        if (rows == null || rows.isEmpty()) {
            errors.add("A hall needs at least one row");
            return errors;
        }
        Set<String> labels = new HashSet<>();
        for (Hall.SeatRow row : rows) {
            String label = row.getLabel();
            if (label == null || label.isBlank()) {
                errors.add("Row label is required");
                continue;
            }
            if (!labels.add(label)) {
                errors.add("Duplicate row label: " + label);
            }
            if (row.getSeats() == null || row.getSeats().isEmpty()) {
                errors.add("Row " + label + " needs at least one seat");
                continue;
            }
            Set<Integer> numbers = new HashSet<>();
            for (Hall.Seat seat : row.getSeats()) {
                if (seat.getNumber() < 1) {
                    errors.add("Seat number must be at least 1 in row " + label);
                } else if (!numbers.add(seat.getNumber())) {
                    errors.add("Duplicate seat " + label + seat.getNumber());
                }
            }
        }
        return errors;
    }

    /** End = start + movie duration + ads. */
    public Instant calculateEnd(Instant startsAt, int durationMinutes) {
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be positive: " + durationMinutes);
        }
        return startsAt.plus(Duration.ofMinutes((long) durationMinutes + AD_MINUTES));
    }

    /** Back-to-back showtimes (one ends exactly when the next starts) do not overlap. */
    public boolean overlaps(Instant start1, Instant end1, Instant start2, Instant end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}