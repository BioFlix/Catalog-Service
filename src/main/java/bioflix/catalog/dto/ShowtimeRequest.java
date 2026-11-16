package bioflix.catalog.dto;

import bioflix.catalog.entity.SeatType;
import bioflix.catalog.entity.ShowFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Map;

public record ShowtimeRequest(
        @NotBlank String movieId,
        @NotBlank String hallId,
        @NotNull @Future Instant startsAt,
        @NotNull ShowFormat format,
        @NotBlank String language,
        String subtitles,
        @NotEmpty Map<SeatType, Integer> prices
) {}
