package bioflix.catalog.service;

import bioflix.catalog.dto.MovieDtos.MovieRequest;
import bioflix.catalog.dto.MovieDtos.MovieResponse;
import bioflix.catalog.entity.Movie;
import bioflix.catalog.exception.NotFoundException;
import bioflix.catalog.repository.MovieRepository;
import bioflix.catalog.repository.ShowtimeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;

    public MovieService(MovieRepository movieRepository, ShowtimeRepository showtimeRepository) {
        this.movieRepository = movieRepository;
        this.showtimeRepository = showtimeRepository;
    }

    public List<MovieResponse> getAll(String titleSearch, String genre) {
        List<Movie> movies;
        if (titleSearch != null && !titleSearch.isBlank()) {
            movies = movieRepository.findByTitleContainingIgnoreCase(titleSearch);
        } else if (genre != null && !genre.isBlank()) {
            movies = movieRepository.findByGenresContaining(genre);
        } else {
            movies = movieRepository.findAll();
        }
        return movies.stream().map(MovieResponse::from).toList();
    }

    public MovieResponse getById(String id) {
        return MovieResponse.from(findOrThrow(id));
    }

    public MovieResponse create(MovieRequest request) {
        Movie movie = new Movie(request.title(), request.description(), request.durationMinutes(),
                request.genres() == null ? List.of() : request.genres(), request.ageRating(), request.posterUrl());
        return MovieResponse.from(movieRepository.save(movie));
    }

    public MovieResponse update(String id, MovieRequest request) {
        Movie movie = findOrThrow(id);
        movie.setTitle(request.title());
        movie.setDescription(request.description());
        movie.setDurationMinutes(request.durationMinutes());
        movie.setGenres(request.genres() == null ? List.of() : request.genres());
        movie.setAgeRating(request.ageRating());
        movie.setPosterUrl(request.posterUrl());
        return MovieResponse.from(movieRepository.save(movie));
    }

    public void delete(String id) {
        Movie movie = findOrThrow(id);
        // TODO: decide what happens to existing bookings when a movie is removed
        showtimeRepository.deleteByMovieId(movie.getId());
        movieRepository.delete(movie);
    }

    public Movie findOrThrow(String id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Movie " + id + " not found"));
    }
}
