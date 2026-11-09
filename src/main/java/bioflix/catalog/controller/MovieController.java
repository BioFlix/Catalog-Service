package bioflix.catalog.controller;

import bioflix.catalog.dto.MovieDtos.MovieRequest;
import bioflix.catalog.dto.MovieDtos.MovieResponse;
import bioflix.catalog.dto.ShowtimeDtos.ShowtimeResponse;
import bioflix.catalog.service.MovieService;
import bioflix.catalog.service.ShowtimeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;
    private final ShowtimeService showtimeService;

    public MovieController(MovieService movieService, ShowtimeService showtimeService) {
        this.movieService = movieService;
        this.showtimeService = showtimeService;
    }

    /** GET /api/movies?title=dune  or  /api/movies?genre=Drama */
    @GetMapping
    public List<MovieResponse> getAll(@RequestParam(required = false) String title,
                                      @RequestParam(required = false) String genre) {
        return movieService.getAll(title, genre);
    }

    @GetMapping("/{id}")
    public MovieResponse getById(@PathVariable String id) {
        return movieService.getById(id);
    }

    @GetMapping("/{id}/showtimes")
    public List<ShowtimeResponse> getShowtimes(@PathVariable String id) {
        return showtimeService.getUpcomingForMovie(id);
    }

    // TODO: admin only (check X-User-Role header set by the gateway)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieResponse create(@Valid @RequestBody MovieRequest request) {
        return movieService.create(request);
    }

    @PutMapping("/{id}")
    public MovieResponse update(@PathVariable String id, @Valid @RequestBody MovieRequest request) {
        return movieService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        movieService.delete(id);
    }
}
