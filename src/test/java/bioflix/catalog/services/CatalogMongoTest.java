package bioflix.catalog.services;

import bioflix.catalog.entity.AgeRating;
import bioflix.catalog.entity.Movie;
import bioflix.catalog.entity.MovieStatus;
import bioflix.catalog.entity.Showtime;
import bioflix.catalog.entity.ShowtimeStatus;
import bioflix.catalog.repository.MovieRepository;
import bioflix.catalog.repository.ShowtimeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DuplicateKeyException;
import org.testcontainers.containers.MongoDBContainer;   // 2.x: org.testcontainers.mongodb.MongoDBContainer
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "app.seed-data=false")
@Testcontainers
class CatalogMongoTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @Autowired
    MovieRepository movies;
    @Autowired
    ShowtimeRepository showtimes;

    @AfterEach
    void clean() {
        showtimes.deleteAll();
        movies.deleteAll();
    }

    private Movie movie(String slug) {
        return Movie.builder().title("T").slug(slug).durationMinutes(100)
                .ageRating(AgeRating.A).genres(List.of("Drama"))
                .releaseDate(LocalDate.now()).status(MovieStatus.NOW_SHOWING).build();
    }

    @Test
    @DisplayName("The unique index on slug rejects a second movie with the same slug")
    void slugIsUnique() {
        movies.save(movie("neon-harbor"));

        assertThrows(DuplicateKeyException.class, () -> movies.save(movie("neon-harbor")));
    }

    @Test
    @DisplayName("Only scheduled, future showtimes of the movie are returned, earliest first")
    void upcomingShowtimes() {
        Movie m = movies.save(movie("paper-moons"));
        Instant now = Instant.now();
        Showtime later = showtimes.save(showtime(m.getId(), now.plusSeconds(7200), ShowtimeStatus.SCHEDULED));
        Showtime sooner = showtimes.save(showtime(m.getId(), now.plusSeconds(3600), ShowtimeStatus.SCHEDULED));
        showtimes.save(showtime(m.getId(), now.plusSeconds(1800), ShowtimeStatus.CANCELLED));
        showtimes.save(showtime(m.getId(), now.minusSeconds(3600), ShowtimeStatus.SCHEDULED));

        List<Showtime> result = showtimes.findByMovieIdAndStatusAndStartsAtAfterOrderByStartsAtAsc(
                m.getId(), ShowtimeStatus.SCHEDULED, now);

        assertEquals(List.of(sooner.getId(), later.getId()),
                result.stream().map(Showtime::getId).toList());
    }

    private Showtime showtime(String movieId, Instant startsAt, ShowtimeStatus status) {
        return Showtime.builder().movieId(movieId).hallId("66f000000000000000000001")
                .startsAt(startsAt).endsAt(startsAt.plusSeconds(7200))
                .currency("DKK").status(status).build();
    }
}
