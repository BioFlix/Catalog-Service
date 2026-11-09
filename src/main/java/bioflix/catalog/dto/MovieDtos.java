package bioflix.catalog.dto;

import bioflix.catalog.entity.Movie;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public final class MovieDtos {

    private MovieDtos() {
    }

    public record MovieRequest(
            @NotBlank @Size(max = 200) String title,
            @Size(max = 2000) String description,
            @Min(1) int durationMinutes,
            List<String> genres,
            String ageRating,
            String posterUrl
    ) {
    }

    public record MovieResponse(
            String id,
            String title,
            String description,
            int durationMinutes,
            List<String> genres,
            String ageRating,
            String posterUrl
    ) {
        public static MovieResponse from(Movie movie) {
            return new MovieResponse(movie.getId(), movie.getTitle(), movie.getDescription(),
                    movie.getDurationMinutes(), movie.getGenres(), movie.getAgeRating(), movie.getPosterUrl());
        }
    }
}
