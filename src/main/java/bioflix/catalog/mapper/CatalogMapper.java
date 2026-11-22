package bioflix.catalog.mapper;

import bioflix.catalog.dto.HallRequest;
import bioflix.catalog.dto.HallResponse;
import bioflix.catalog.dto.MovieRequest;
import bioflix.catalog.dto.MovieResponse;
import bioflix.catalog.dto.ShowtimeResponse;
import bioflix.catalog.entity.Hall;
import bioflix.catalog.entity.Movie;
import bioflix.catalog.entity.Showtime;
import org.springframework.stereotype.Component;

import java.util.List;

/** Converts between entities and DTOs, so entities never leave the service layer. */
@Component
public class CatalogMapper {

    // ---- Movie ----

    public Movie toEntity(MovieRequest r) {
        return Movie.builder()
                .title(r.title())
                .slug(r.slug())
                .description(r.description())
                .durationMinutes(r.durationMinutes())
                .ageRating(r.ageRating())
                .genres(r.genres())
                .originalLanguage(r.originalLanguage())
                .releaseDate(r.releaseDate())
                .posterUrl(r.posterUrl())
                .status(r.status())
                .build();
    }

    public void update(Movie m, MovieRequest r) {
        m.setTitle(r.title());
        m.setSlug(r.slug());
        m.setDescription(r.description());
        m.setDurationMinutes(r.durationMinutes());
        m.setAgeRating(r.ageRating());
        m.setGenres(r.genres());
        m.setOriginalLanguage(r.originalLanguage());
        m.setReleaseDate(r.releaseDate());
        m.setPosterUrl(r.posterUrl());
        m.setStatus(r.status());
    }

    public MovieResponse toResponse(Movie m) {
        return new MovieResponse(m.getId(), m.getTitle(), m.getSlug(), m.getDescription(),
                m.getDurationMinutes(), m.getAgeRating(), m.getGenres(), m.getOriginalLanguage(),
                m.getReleaseDate(), m.getPosterUrl(), m.getStatus());
    }

    // ---- Hall ----

    public Hall toEntity(HallRequest r) {
        List<Hall.SeatRow> rows = r.rows().stream()
                .map(row -> new Hall.SeatRow(row.label(),
                        row.seats().stream().map(s -> new Hall.Seat(s.number(), s.type())).toList()))
                .toList();
        int capacity = rows.stream().mapToInt(row -> row.getSeats().size()).sum();
        return Hall.builder()
                .name(r.name())
                .cinema(new Hall.CinemaInfo(r.cinemaName(), r.city()))
                .capacity(capacity)
                .rows(rows)
                .build();
    }

    public HallResponse toResponse(Hall h) {
        List<HallResponse.RowDto> rows = h.getRows().stream()
                .map(row -> new HallResponse.RowDto(row.getLabel(),
                        row.getSeats().stream()
                                .map(s -> new HallResponse.SeatDto(s.getNumber(), s.getType())).toList()))
                .toList();
        return new HallResponse(h.getId(), h.getName(), h.getCinema().getName(),
                h.getCinema().getCity(), h.getCapacity(), rows);
    }

    // ---- Showtime ----

    public ShowtimeResponse toResponse(Showtime s, Movie movie, Hall hall) {
        return new ShowtimeResponse(s.getId(), s.getMovieId(), movie.getTitle(), s.getHallId(),
                hall.getName(), hall.getCinema().getName(), s.getStartsAt(), s.getEndsAt(),
                s.getFormat(), s.getLanguage(), s.getSubtitles(), s.getPrices(),
                s.getCurrency(), s.getStatus());
    }
}
