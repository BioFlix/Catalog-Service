package bioflix.catalog.services;

import bioflix.catalog.entity.Hall;
import bioflix.catalog.entity.SeatType;
import bioflix.catalog.service.CatalogValidation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogValidationTest {

    private final CatalogValidation validator = new CatalogValidation();

    // ---------------------------------------------------------------- slug

    @ParameterizedTest
    @ValueSource(strings = {
            "a",                // Valid: 1 char lower boundary
            "ab",               // Valid: 2 chars
            "neon-harbor",      // Valid: letters with hyphen
            "neon-harbor-2",    // Valid: letters, hyphens and digit
            "2001"              // Valid: digits only
    })
    @DisplayName("Valid slugs return no errors")
    void validateSlug_validValues(String slug) {
        List<String> errors = validator.validateSlug(slug);

        assertTrue(errors.isEmpty(), "Should have no errors for: " + slug);
    }

    @Test
    @DisplayName("Slug of 79 chars is valid")
    void validateSlug_79CharsValid() {
        assertTrue(validator.validateSlug("a".repeat(79)).isEmpty());
    }

    @Test
    @DisplayName("Slug of 80 chars (upper boundary) is valid")
    void validateSlug_upperBoundaryValid() {
        assertTrue(validator.validateSlug("a".repeat(80)).isEmpty());
    }

    @Test
    @DisplayName("Slug of 81 chars is invalid")
    void validateSlug_aboveUpperBoundaryInvalid() {
        assertFalse(validator.validateSlug("a".repeat(81)).isEmpty());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "",                 // Invalid: Empty (0 chars lower boundary)
            "Neon",             // Invalid: Uppercase letter
            "NEON",             // Invalid: All uppercase
            "neon harbor",      // Invalid: Whitespace
            "neon_harbor",      // Invalid: Underscore
            "neon!",            // Invalid: Special character
            "-neon",            // Invalid: Leading hyphen
            "neon-",            // Invalid: Trailing hyphen
            "neon--harbor",     // Invalid: Double hyphen
            "nøn"               // Invalid: Non-ASCII (Danish letter)
    })
    @DisplayName("Invalid slugs return error list")
    void validateSlug_invalidValues(String slug) {
        List<String> errors = validator.validateSlug(slug);

        assertFalse(errors.isEmpty(), "Should have errors for: " + slug);
    }

    // ------------------------------------------------------------ duration

    @ParameterizedTest
    @ValueSource(ints = {
            30,     // Valid: lower boundary
            31,     // Valid: just above lower boundary
            166,    // Valid: middle value
            299,    // Valid: just below upper boundary
            300     // Valid: upper boundary
    })
    @DisplayName("Valid durations return no errors")
    void validateDuration_validValues(int minutes) {
        assertTrue(validator.validateDuration(minutes).isEmpty(), "Should have no errors for: " + minutes);
    }

    @ParameterizedTest
    @ValueSource(ints = {
            29,                 // Invalid: just below lower boundary
            15,                 // Invalid: middle of 1-29
            1,                  // Invalid: shortest positive
            0,                  // Invalid: zero
            -1,                 // Invalid: negative
            301,                // Invalid: just above upper boundary
            500,                // Invalid: middle of 301-MAX
            Integer.MAX_VALUE   // Invalid: extreme
    })
    @DisplayName("Invalid durations return error list")
    void validateDuration_invalidValues(int minutes) {
        assertFalse(validator.validateDuration(minutes).isEmpty(), "Should have errors for: " + minutes);
    }

    // ------------------------------------------------------------- end time

    @ParameterizedTest
    @CsvSource({
            "1,   2026-11-17T17:56:00Z",   // 1 + 25 min
            "2,   2026-11-17T17:57:00Z",   // 2 + 25 min
            "30,  2026-11-17T18:25:00Z",   // Lower boundary: 30 + 25 min
            "166, 2026-11-17T20:41:00Z",   // Middle value:   166 + 25 min
            "300, 2026-11-17T22:55:00Z"    // Upper boundary: 300 + 25 min
    })
    @DisplayName("End time is start + duration + 25 minutes of ads")
    void calculateEnd_addsDurationAndAds(int minutes, Instant expectedEnd) {
        Instant start = Instant.parse("2026-11-17T17:30:00Z");

        assertEquals(expectedEnd, validator.calculateEnd(start, minutes));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("End time rejects a non-positive duration")
    void calculateEnd_invalidDuration(int minutes) {
        assertThrows(IllegalArgumentException.class,
                () -> validator.calculateEnd(Instant.parse("2026-11-17T17:30:00Z"), minutes));
    }

    // -------------------------------------------------------------- overlap
    // Existing showtime in the hall: 18:00 - 20:00

    @ParameterizedTest
    @CsvSource({
            "2026-11-17T16:00:00Z, 2026-11-17T17:59:59Z, false",   // Ends 1 second before existing starts
            "2026-11-17T16:00:00Z, 2026-11-17T18:00:00Z, false",   // Ends exactly when existing starts
            "2026-11-17T16:00:00Z, 2026-11-17T18:00:01Z, true",    // Ends 1 second into existing
            "2026-11-17T20:00:01Z, 2026-11-17T22:00:00Z, false",   // Starts 1 second after existing ends
            "2026-11-17T20:00:00Z, 2026-11-17T22:00:00Z, false",   // Starts exactly when existing ends
            "2026-11-17T19:59:59Z, 2026-11-17T22:00:00Z, true",    // Starts 1 second before existing ends
            "2026-11-17T19:00:00Z, 2026-11-17T22:00:00Z, true",    // Starts inside, ends after
            "2026-11-17T16:00:00Z, 2026-11-17T19:00:00Z, true",    // Starts before, ends inside
            "2026-11-17T18:30:00Z, 2026-11-17T19:30:00Z, true",    // Inside existing
            "2026-11-17T17:00:00Z, 2026-11-17T21:00:00Z, true",    // Around existing
            "2026-11-17T18:00:00Z, 2026-11-17T20:00:00Z, true",    // Identical
            "2026-11-17T10:00:00Z, 2026-11-17T12:00:00Z, false",   // Well before
            "2026-11-17T22:00:00Z, 2026-11-17T23:00:00Z, false"    // Well after
    })
    @DisplayName("Overlap detection, including back-to-back boundaries")
    void overlaps(Instant newStart, Instant newEnd, boolean expected) {
        Instant existingStart = Instant.parse("2026-11-17T18:00:00Z");
        Instant existingEnd = Instant.parse("2026-11-17T20:00:00Z");

        assertEquals(expected, validator.overlaps(newStart, newEnd, existingStart, existingEnd));
    }

    // --------------------------------------------------------------- prices

    private static final EnumSet<SeatType> HALL_TYPES = EnumSet.of(SeatType.STANDARD, SeatType.VIP);

    @Test
    @DisplayName("Prices for every seat type in the hall are valid")
    void validatePrices_valid() {
        Map<SeatType, Integer> prices = Map.of(SeatType.STANDARD, 13500, SeatType.VIP, 17500);

        assertTrue(validator.validatePrices(prices, HALL_TYPES).isEmpty());
    }

    @Test
    @DisplayName("Price of 1 øre (lower boundary) is valid")
    void validatePrices_lowerBoundaryValid() {
        Map<SeatType, Integer> prices = Map.of(SeatType.STANDARD, 1, SeatType.VIP, 1);

        assertTrue(validator.validatePrices(prices, HALL_TYPES).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -13500})
    @DisplayName("Zero or negative price is invalid")
    void validatePrices_nonPositive(int price) {
        Map<SeatType, Integer> prices = Map.of(SeatType.STANDARD, price, SeatType.VIP, 17500);

        assertFalse(validator.validatePrices(prices, HALL_TYPES).isEmpty());
    }

    @Test
    @DisplayName("Missing price for a seat type in the hall is invalid")
    void validatePrices_missingType() {
        Map<SeatType, Integer> prices = Map.of(SeatType.STANDARD, 13500);

        assertFalse(validator.validatePrices(prices, HALL_TYPES).isEmpty());
    }

    @Test
    @DisplayName("Extra price for a seat type the hall does not have is fine")
    void validatePrices_extraTypeAllowed() {
        Map<SeatType, Integer> prices =
                Map.of(SeatType.STANDARD, 13500, SeatType.VIP, 17500, SeatType.WHEELCHAIR, 13500);

        assertTrue(validator.validatePrices(prices, HALL_TYPES).isEmpty());
    }

    @Test
    @DisplayName("Null or empty prices are invalid")
    void validatePrices_nullOrEmpty() {
        assertFalse(validator.validatePrices(null, HALL_TYPES).isEmpty());
        assertFalse(validator.validatePrices(Map.of(), HALL_TYPES).isEmpty());
    }

    // ---------------------------------------------------------- seat layout

    private static Hall.SeatRow row(String label, int... numbers) {
        List<Hall.Seat> seats = new ArrayList<>();
        for (int n : numbers) {
            seats.add(new Hall.Seat(n, SeatType.STANDARD));
        }
        return new Hall.SeatRow(label, seats);
    }

    @Test
    @DisplayName("Valid layout: unique rows and unique seats")
    void validateSeatLayout_valid() {
        List<Hall.SeatRow> rows = List.of(row("A", 1, 2, 3), row("B", 1, 2, 3));

        assertTrue(validator.validateSeatLayout(rows).isEmpty());
    }

    @Test
    @DisplayName("Smallest valid hall: one row with one seat")
    void validateSeatLayout_smallestHall() {
        assertTrue(validator.validateSeatLayout(List.of(row("A", 1))).isEmpty());
    }

    @Test
    @DisplayName("Same seat number in different rows is fine")
    void validateSeatLayout_sameNumberDifferentRows() {
        assertTrue(validator.validateSeatLayout(List.of(row("A", 1), row("B", 1))).isEmpty());
    }

    @Test
    @DisplayName("Row with two seats is valid")
    void validateSeatLayout_twoSeats() {
        assertTrue(validator.validateSeatLayout(List.of(row("A", 1, 2))).isEmpty());
    }

    @Test
    @DisplayName("Gaps in seat numbers (aisles) are allowed")
    void validateSeatLayout_gapsAllowed() {
        assertTrue(validator.validateSeatLayout(List.of(row("A", 1, 2, 5, 6))).isEmpty());
    }

    @Test
    @DisplayName("Duplicate row label is invalid")
    void validateSeatLayout_duplicateRow() {
        assertFalse(validator.validateSeatLayout(List.of(row("A", 1, 2), row("A", 3, 4))).isEmpty());
    }

    @Test
    @DisplayName("Duplicate seat number in a row is invalid")
    void validateSeatLayout_duplicateSeat() {
        assertFalse(validator.validateSeatLayout(List.of(row("A", 1, 2, 2))).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("Seat number below 1 is invalid")
    void validateSeatLayout_seatNumberBelowOne(int number) {
        assertFalse(validator.validateSeatLayout(List.of(row("A", number))).isEmpty());
    }

    @Test
    @DisplayName("Missing hall parts are invalid: no rows, no seats, blank label")
    void validateSeatLayout_missingParts() {
        assertFalse(validator.validateSeatLayout(null).isEmpty());                      // null rows
        assertFalse(validator.validateSeatLayout(List.of()).isEmpty());                 // 0 rows
        assertFalse(validator.validateSeatLayout(List.of(row("A"))).isEmpty());         // row with 0 seats
        assertFalse(validator.validateSeatLayout(List.of(row("", 1, 2))).isEmpty());    // blank label
        assertFalse(validator.validateSeatLayout(List.of(row(" ", 1, 2))).isEmpty());   // whitespace label
    }
}