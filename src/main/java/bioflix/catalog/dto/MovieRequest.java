package bioflix.catalog.dto;

import bioflix.catalog.entity.AgeRating;
import bioflix.catalog.entity.MovieStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

public record MovieRequest(
        @NotBlank String title,
        @NotBlank String slug,
        String description,
        @Positive int durationMinutes,
        @NotNull AgeRating ageRating,
        List<String> genres,
        String originalLanguage,
        LocalDate releaseDate,
        String posterUrl,
        @NotNull MovieStatus status
) {}
