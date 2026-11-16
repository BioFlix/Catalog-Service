package bioflix.catalog.service;

import bioflix.catalog.dto.MovieRequest;
import bioflix.catalog.dto.MovieResponse;
import bioflix.catalog.entity.Movie;
import bioflix.catalog.entity.MovieStatus;
import bioflix.catalog.exception.ConflictException;
import bioflix.catalog.exception.NotFoundException;
import bioflix.catalog.mapper.CatalogMapper;
import bioflix.catalog.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movies;
    private final CatalogMapper mapper;

    public List<MovieResponse> list(MovieStatus status) {
        List<Movie> result = status == null
                ? movies.findAll()
                : movies.findByStatusOrderByReleaseDateDesc(status);
        return result.stream().map(mapper::toResponse).toList();
    }

    public MovieResponse get(String id) {
        return mapper.toResponse(find(id));
    }

    public MovieResponse getBySlug(String slug) {
        return movies.findBySlug(slug)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Movie", slug));
    }

    public MovieResponse create(MovieRequest request) {
        if (movies.existsBySlug(request.slug())) {
            throw new ConflictException("Slug already in use: " + request.slug());
        }
        return mapper.toResponse(movies.save(mapper.toEntity(request)));
    }

    public MovieResponse update(String id, MovieRequest request) {
        Movie movie = find(id);
        if (!movie.getSlug().equals(request.slug()) && movies.existsBySlug(request.slug())) {
            throw new ConflictException("Slug already in use: " + request.slug());
        }
        mapper.update(movie, request);
        return mapper.toResponse(movies.save(movie));
    }

    public void delete(String id) {
        movies.delete(find(id));
    }

    /** Used by other services in this module, which need the entity. */
    Movie find(String id) {
        return movies.findById(id).orElseThrow(() -> new NotFoundException("Movie", id));
    }
}
