package bioflix.catalog.dto;

import bioflix.catalog.entity.AgeRating;
import bioflix.catalog.entity.MovieStatus;

import java.time.LocalDate;
import java.util.List;

public record MovieResponse(
        String id,
        String title,
        String slug,
        String description,
        int durationMinutes,
        AgeRating ageRating,
        List<String> genres,
        String originalLanguage,
        LocalDate releaseDate,
        String posterUrl,
        MovieStatus status
) {}
