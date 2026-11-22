package bioflix.catalog.repository;

import bioflix.catalog.entity.Showtime;
import bioflix.catalog.entity.ShowtimeStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface ShowtimeRepository extends MongoRepository<Showtime, String> {

    List<Showtime> findByMovieIdAndStatusAndStartsAtAfterOrderByStartsAtAsc(
            String movieId, ShowtimeStatus status, Instant after);

    /** Finds scheduled showtimes in a hall that overlap the given time window. */
    @Query("{ 'hallId': ?0, 'status': 'SCHEDULED', 'startsAt': { $lt: ?2 }, 'endsAt': { $gt: ?1 } }")
    List<Showtime> findOverlapping(String hallId, Instant start, Instant end);
}
