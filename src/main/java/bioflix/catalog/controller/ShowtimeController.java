package bioflix.catalog.controller;

import bioflix.catalog.dto.ShowtimeRequest;
import bioflix.catalog.dto.ShowtimeResponse;
import bioflix.catalog.service.ShowtimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService service;

    @GetMapping("/movies/{movieId}/showtimes")
    public List<ShowtimeResponse> forMovie(@PathVariable String movieId) {
        return service.listForMovie(movieId);
    }

    @GetMapping("/showtimes/{id}")
    public ShowtimeResponse get(@PathVariable String id) {
        return service.get(id);
    }

    @PostMapping("/showtimes")
    @ResponseStatus(HttpStatus.CREATED)
    public ShowtimeResponse create(@Valid @RequestBody ShowtimeRequest request) {
        return service.create(request);
    }

    @PostMapping("/showtimes/{id}/cancel")
    public ShowtimeResponse cancel(@PathVariable String id) {
        return service.cancel(id);
    }
}
