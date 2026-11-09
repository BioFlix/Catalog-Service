package bioflix.catalog.dto;

import bioflix.catalog.entity.Hall;
import bioflix.catalog.entity.Movie;
import bioflix.catalog.entity.Showtime;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class ShowtimeDtos {

    private ShowtimeDtos() {
    }

    public record ShowtimeRequest(
            @NotBlank String movieId,
            @NotBlank String hallId,
            @NotNull @Future Instant startTime,
            @Positive long priceMinor,
            @NotBlank @Size(min = 3, max = 3) String currency
    ) {
    }

    /** Showtime enriched with movie title and hall name, so the client needs only one call. */
    public record ShowtimeResponse(
            String id,
            String movieId,
            String movieTitle,
            String hallId,
            String hallName,
            Instant startTime,
            long priceMinor,
            String currency
    ) {
        public static ShowtimeResponse from(Showtime showtime, Movie movie, Hall hall) {
            return new ShowtimeResponse(showtime.getId(), movie.getId(), movie.getTitle(),
                    hall.getId(), hall.getName(), showtime.getStartTime(),
                    showtime.getPriceMinor(), showtime.getCurrency());
        }
    }
}
