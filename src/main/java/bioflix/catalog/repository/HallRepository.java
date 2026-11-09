package bioflix.catalog.repository;

import bioflix.catalog.entity.Hall;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HallRepository extends MongoRepository<Hall, String> {

    boolean existsByName(String name);
}
