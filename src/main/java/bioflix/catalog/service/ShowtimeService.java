package bioflix.catalog.service;

import bioflix.catalog.dto.ShowtimeDtos.ShowtimeRequest;
import bioflix.catalog.dto.ShowtimeDtos.ShowtimeResponse;
import bioflix.catalog.entity.Hall;
import bioflix.catalog.entity.Movie;
import bioflix.catalog.entity.Showtime;
import bioflix.catalog.exception.ConflictException;
import bioflix.catalog.exception.NotFoundException;
import bioflix.catalog.repository.ShowtimeRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class ShowtimeService {

    private static final ZoneId CINEMA_ZONE = ZoneId.of("Europe/Copenhagen");
    private static final Duration CLEANING_BUFFER = Duration.ofMinutes(15);

    private final ShowtimeRepository showtimeRepository;
    private final MovieService movieService;
    private final HallService hallService;

    public ShowtimeService(ShowtimeRepository showtimeRepository, MovieService movieService, HallService hallService) {
        this.showtimeRepository = showtimeRepository;
        this.movieService = movieService;
        this.hallService = hallService;
    }

    /** Upcoming showtimes for a movie. */
    public List<ShowtimeResponse> getUpcomingForMovie(String movieId) {
        return showtimeRepository.findByMovieIdAndStartTimeAfterOrderByStartTimeAsc(movieId, Instant.now())
                .stream().map(this::toResponse).toList();
    }

    /** All showtimes on a given date (Danish time). */
    public List<ShowtimeResponse> getForDate(LocalDate date) {
        Instant from = date.atStartOfDay(CINEMA_ZONE).toInstant();
        Instant to = date.plusDays(1).atStartOfDay(CINEMA_ZONE).toInstant();
        return showtimeRepository.findByStartTimeBetweenOrderByStartTimeAsc(from, to)
                .stream().map(this::toResponse).toList();
    }

    public ShowtimeResponse getById(String id) {
        return toResponse(findOrThrow(id));
    }

    public ShowtimeResponse create(ShowtimeRequest request) {
        Movie movie = movieService.findOrThrow(request.movieId());
        Hall hall = hallService.findOrThrow(request.hallId());

        Instant start = request.startTime();
        Instant end = start.plus(Duration.ofMinutes(movie.getDurationMinutes())).plus(CLEANING_BUFFER);

        // Simple overlap check: no other showtime may start in the same hall while this one runs.
        // (Does not catch a longer movie that started earlier – good enough for a start.)
        if (!showtimeRepository.findByHallIdAndStartTimeBetween(hall.getId(), start, end).isEmpty()) {
            throw new ConflictException("Hall " + hall.getName() + " is already booked in that time slot");
        }

        Showtime showtime = new Showtime(movie.getId(), hall.getId(), start, request.priceMinor(),
                request.currency().toUpperCase());
        return ShowtimeResponse.from(showtimeRepository.save(showtime), movie, hall);
    }

    public void delete(String id) {
        showtimeRepository.delete(findOrThrow(id));
    }

    public Showtime findOrThrow(String id) {
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Showtime " + id + " not found"));
    }

    private ShowtimeResponse toResponse(Showtime showtime) {
        Movie movie = movieService.findOrThrow(showtime.getMovieId());
        Hall hall = hallService.findOrThrow(showtime.getHallId());
        return ShowtimeResponse.from(showtime, movie, hall);
    }
}
