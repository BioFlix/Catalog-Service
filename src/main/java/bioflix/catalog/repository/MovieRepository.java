package bioflix.catalog.repository;

import bioflix.catalog.entity.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MovieRepository extends MongoRepository<Movie, String> {

    List<Movie> findByTitleContainingIgnoreCase(String title);

    List<Movie> findByGenresContaining(String genre);
}
