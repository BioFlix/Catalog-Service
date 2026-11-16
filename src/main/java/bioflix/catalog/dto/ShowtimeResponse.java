package bioflix.catalog.dto;

import bioflix.catalog.entity.SeatType;
import bioflix.catalog.entity.ShowFormat;
import bioflix.catalog.entity.ShowtimeStatus;

import java.time.Instant;
import java.util.Map;

public record ShowtimeResponse(
        String id,
        String movieId,
        String movieTitle,
        String hallId,
        String hallName,
        String cinemaName,
        Instant startsAt,
        Instant endsAt,
        ShowFormat format,
        String language,
        String subtitles,
        Map<SeatType, Integer> prices,
        String currency,
        ShowtimeStatus status
) {}
