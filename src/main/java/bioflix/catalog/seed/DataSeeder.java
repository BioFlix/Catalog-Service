package bioflix.catalog.seed;

import bioflix.catalog.entity.AgeRating;
import bioflix.catalog.entity.Hall;
import bioflix.catalog.entity.Movie;
import bioflix.catalog.entity.MovieStatus;
import bioflix.catalog.entity.SeatType;
import bioflix.catalog.entity.ShowFormat;
import bioflix.catalog.entity.Showtime;
import bioflix.catalog.entity.ShowtimeStatus;
import bioflix.catalog.repository.HallRepository;
import bioflix.catalog.repository.MovieRepository;
import bioflix.catalog.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Inserts demo data when app.seed-data=true and the database is empty. */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true")
public class DataSeeder implements ApplicationRunner {

    private static final int BUFFER_MINUTES = 25;
    private static final Map<SeatType, Integer> PRICES = Map.of(
            SeatType.STANDARD, 13500,
            SeatType.VIP, 17500,
            SeatType.WHEELCHAIR, 13500);

    private final MovieRepository movies;
    private final HallRepository halls;
    private final ShowtimeRepository showtimes;

    @Override
    public void run(ApplicationArguments args) {
        if (movies.count() > 0 || halls.count() > 0 || showtimes.count() > 0) {
            log.info("Seeder skipped: catalog database already has data");
            return;
        }

        Movie lighthouse = movies.save(movie("The Last Lighthouse", "the-last-lighthouse",
                166, AgeRating.AGE_11, List.of("Drama", "Mystery"), MovieStatus.NOW_SHOWING));
        Movie neon = movies.save(movie("Neon Harbor", "neon-harbor",
                120, AgeRating.AGE_15, List.of("Action", "Thriller"), MovieStatus.NOW_SHOWING));
        movies.save(movie("Paper Moons", "paper-moons",
                95, AgeRating.A, List.of("Animation", "Family"), MovieStatus.COMING_SOON));

        Hall sal1 = halls.save(hall("Sal 1", "Bioflix Nørreport", "København", 6, 10));
        Hall sal2 = halls.save(hall("Sal 2", "Bioflix Nørreport", "København", 4, 8));

        List<Showtime> toSave = new ArrayList<>();
        for (int day = 1; day <= 3; day++) {
            toSave.add(showtime(lighthouse, sal1, day, LocalTime.of(16, 0), ShowFormat.TWO_D));
            toSave.add(showtime(lighthouse, sal1, day, LocalTime.of(19, 30), ShowFormat.TWO_D));
            toSave.add(showtime(neon, sal2, day, LocalTime.of(17, 0), ShowFormat.IMAX));
            toSave.add(showtime(neon, sal2, day, LocalTime.of(20, 30), ShowFormat.IMAX));
        }
        List<Showtime> saved = showtimes.saveAll(toSave);

        log.info("Seeded 3 movies, 2 halls and {} showtimes. First showtime id for testing: {}",
                saved.size(), saved.get(0).getId());
    }

    private Movie movie(String title, String slug, int minutes, AgeRating rating,
                        List<String> genres, MovieStatus status) {
        return Movie.builder()
                .title(title)
                .slug(slug)
                .description("Demo movie: " + title)
                .durationMinutes(minutes)
                .ageRating(rating)
                .genres(genres)
                .originalLanguage("en")
                .releaseDate(LocalDate.now())
                .status(status)
                .build();
    }

    /** Row A starts with a wheelchair seat, the last row is VIP, everything else is standard. */
    private Hall hall(String name, String cinemaName, String city, int rowCount, int seatsPerRow) {
        List<Hall.SeatRow> rows = new ArrayList<>();
        for (int r = 0; r < rowCount; r++) {
            String label = String.valueOf((char) ('A' + r));
            List<Hall.Seat> seats = new ArrayList<>();
            for (int n = 1; n <= seatsPerRow; n++) {
                SeatType type = SeatType.STANDARD;
                if (r == 0 && n == 1) {
                    type = SeatType.WHEELCHAIR;
                } else if (r == rowCount - 1) {
                    type = SeatType.VIP;
                }
                seats.add(new Hall.Seat(n, type));
            }
            rows.add(new Hall.SeatRow(label, seats));
        }
        return Hall.builder()
                .name(name)
                .cinema(new Hall.CinemaInfo(cinemaName, city))
                .capacity(rowCount * seatsPerRow)
                .rows(rows)
                .build();
    }

    private Showtime showtime(Movie movie, Hall hall, int daysAhead, LocalTime time, ShowFormat format) {
        Instant startsAt = LocalDateTime.of(LocalDate.now(ZoneOffset.UTC).plusDays(daysAhead), time)
                .toInstant(ZoneOffset.UTC);
        Instant endsAt = startsAt.plus(Duration.ofMinutes(movie.getDurationMinutes() + BUFFER_MINUTES));
        return Showtime.builder()
                .movieId(movie.getId())
                .hallId(hall.getId())
                .startsAt(startsAt)
                .endsAt(endsAt)
                .format(format)
                .language("en")
                .subtitles("da")
                .prices(PRICES)
                .currency("DKK")
                .status(ShowtimeStatus.SCHEDULED)
                .build();
    }
}