package bioflix.catalog.repository;

import bioflix.catalog.entity.Hall;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HallRepository extends MongoRepository<Hall, String> {

    List<Hall> findByCinemaCity(String city);
}
