package bioflix.catalog.service;

import bioflix.catalog.dto.HallResponse;
import bioflix.catalog.dto.ShowtimeDetails;
import bioflix.catalog.dto.ShowtimeRequest;
import bioflix.catalog.dto.ShowtimeResponse;
import bioflix.catalog.entity.Hall;
import bioflix.catalog.entity.Movie;
import bioflix.catalog.entity.SeatType;
import bioflix.catalog.entity.Showtime;
import bioflix.catalog.entity.ShowtimeStatus;
import bioflix.catalog.exception.BusinessRuleException;
import bioflix.catalog.exception.ConflictException;
import bioflix.catalog.exception.NotFoundException;
import bioflix.catalog.mapper.CatalogMapper;
import bioflix.catalog.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ShowtimeService {

    private static final String CURRENCY = "DKK";

    private final ShowtimeRepository showtimes;
    private final MovieService movieService;
    private final HallService hallService;
    private final CatalogMapper mapper;
    private final CatalogValidation validation;

    public List<ShowtimeResponse> listForMovie(String movieId) {
        Movie movie = movieService.find(movieId);
        return showtimes
                .findByMovieIdAndStatusAndStartsAtAfterOrderByStartsAtAsc(
                        movieId, ShowtimeStatus.SCHEDULED, Instant.now())
                .stream()
                .map(s -> mapper.toResponse(s, movie, hallService.find(s.getHallId())))
                .toList();
    }

    public ShowtimeResponse get(String id) {
        Showtime showtime = find(id);
        return toResponse(showtime);
    }

    /** One call with everything Booking needs. Used by the gRPC endpoint. */
    public ShowtimeDetails getDetails(String id) {
        Showtime showtime = find(id);
        Hall hall = hallService.find(showtime.getHallId());
        Movie movie = movieService.find(showtime.getMovieId());
        HallResponse hallResponse = mapper.toResponse(hall);
        return new ShowtimeDetails(mapper.toResponse(showtime, movie, hall), hallResponse);
    }

    public ShowtimeResponse create(ShowtimeRequest request) {
        Movie movie = movieService.find(request.movieId());
        Hall hall = hallService.find(request.hallId());
        validatePrices(request, hall);

        Instant endsAt = validation.calculateEnd(request.startsAt(), movie.getDurationMinutes());

        if (!showtimes.findOverlapping(hall.getId(), request.startsAt(), endsAt).isEmpty()) {
            throw new ConflictException("Hall " + hall.getName() + " is already booked at that time");
        }

        Showtime saved = showtimes.save(Showtime.builder()
                .movieId(movie.getId())
                .hallId(hall.getId())
                .startsAt(request.startsAt())
                .endsAt(endsAt)
                .format(request.format())
                .language(request.language())
                .subtitles(request.subtitles())
                .prices(request.prices())
                .currency(CURRENCY)
                .status(ShowtimeStatus.SCHEDULED)
                .build());
        return mapper.toResponse(saved, movie, hall);
    }

    public ShowtimeResponse cancel(String id) {
        Showtime showtime = find(id);
        showtime.setStatus(ShowtimeStatus.CANCELLED);
        return toResponse(showtimes.save(showtime));
    }

    private void validatePrices(ShowtimeRequest request, Hall hall) {
        Set<SeatType> needed = EnumSet.noneOf(SeatType.class);
        hall.getRows().forEach(row -> row.getSeats().forEach(seat -> needed.add(seat.getType())));

        List<String> errors = validation.validatePrices(request.prices(), needed);
        if (!errors.isEmpty()) {
            throw new BusinessRuleException(String.join("; ", errors));
        }
    }

    private ShowtimeResponse toResponse(Showtime s) {
        return mapper.toResponse(s, movieService.find(s.getMovieId()), hallService.find(s.getHallId()));
    }

    private Showtime find(String id) {
        return showtimes.findById(id).orElseThrow(() -> new NotFoundException("Showtime", id));
    }
}