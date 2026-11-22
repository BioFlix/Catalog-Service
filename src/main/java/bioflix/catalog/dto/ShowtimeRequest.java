package bioflix.catalog.dto;

import bioflix.catalog.entity.SeatType;
import bioflix.catalog.entity.ShowFormat;
import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.Map;

public record ShowtimeRequest(
        @NotBlank String movieId,
        @NotBlank String hallId,
        @NotNull @Future Instant startsAt,
        @NotNull ShowFormat format,
        @NotBlank String language,
        String subtitles,
        @NotEmpty Map<SeatType, @NotNull @Positive Integer> prices
) {}
