package bioflix.catalog.repository;

import bioflix.catalog.entity.Showtime;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface ShowtimeRepository extends MongoRepository<Showtime, String> {

    List<Showtime> findByMovieIdAndStartTimeAfterOrderByStartTimeAsc(String movieId, Instant after);

    List<Showtime> findByStartTimeBetweenOrderByStartTimeAsc(Instant from, Instant to);

    List<Showtime> findByHallIdAndStartTimeBetween(String hallId, Instant from, Instant to);

    void deleteByMovieId(String movieId);
}
