package bioflix.catalog.repository;

import bioflix.catalog.entity.Movie;
import bioflix.catalog.entity.MovieStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends MongoRepository<Movie, String> {

    Optional<Movie> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Movie> findByStatusOrderByReleaseDateDesc(MovieStatus status);
}
