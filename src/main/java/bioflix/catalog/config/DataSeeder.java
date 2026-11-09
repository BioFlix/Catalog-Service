package bioflix.catalog.config;

import bioflix.catalog.entity.Hall;
import bioflix.catalog.entity.Movie;
import bioflix.catalog.entity.Showtime;
import bioflix.catalog.repository.HallRepository;
import bioflix.catalog.repository.MovieRepository;
import bioflix.catalog.repository.ShowtimeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

/** Demo data so the other services have something to work with. Disable with app.seed-data=false. */
@Component
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final MovieRepository movieRepository;
    private final HallRepository hallRepository;
    private final ShowtimeRepository showtimeRepository;

    public DataSeeder(MovieRepository movieRepository, HallRepository hallRepository,
                      ShowtimeRepository showtimeRepository) {
        this.movieRepository = movieRepository;
        this.hallRepository = hallRepository;
        this.showtimeRepository = showtimeRepository;
    }

    @Override
    public void run(String... args) {
        if (movieRepository.count() > 0) {
            return;
        }

        Hall hall1 = hallRepository.save(new Hall("Sal 1", 10, 14));
        Hall hall2 = hallRepository.save(new Hall("Sal 2", 6, 10));

        Movie m1 = movieRepository.save(new Movie("Nattevagten", "Dansk thriller.", 107,
                List.of("Thriller"), "15", null));
        Movie m2 = movieRepository.save(new Movie("Robotternes By", "Animationsfilm for hele familien.", 92,
                List.of("Animation", "Familie"), "A", null));

        ZoneId zone = ZoneId.of("Europe/Copenhagen");
        LocalDate tomorrow = LocalDate.now(zone).plusDays(1);

        showtimeRepository.saveAll(List.of(
                new Showtime(m1.getId(), hall1.getId(), tomorrow.atTime(LocalTime.of(19, 0)).atZone(zone).toInstant(), 12500, "DKK"),
                new Showtime(m1.getId(), hall1.getId(), tomorrow.atTime(LocalTime.of(21, 30)).atZone(zone).toInstant(), 12500, "DKK"),
                new Showtime(m2.getId(), hall2.getId(), tomorrow.atTime(LocalTime.of(16, 0)).atZone(zone).toInstant(), 9500, "DKK")
        ));

        log.info("Seeded demo data: 2 halls, 2 movies, 3 showtimes");
    }
}
