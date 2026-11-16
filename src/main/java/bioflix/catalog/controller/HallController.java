package bioflix.catalog.controller;

import bioflix.catalog.dto.HallRequest;
import bioflix.catalog.dto.HallResponse;
import bioflix.catalog.service.HallService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/halls")
@RequiredArgsConstructor
public class HallController {

    private final HallService service;

    @GetMapping
    public List<HallResponse> list(@RequestParam(required = false) String city) {
        return service.list(city);
    }

    @GetMapping("/{id}")
    public HallResponse get(@PathVariable String id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HallResponse create(@Valid @RequestBody HallRequest request) {
        return service.create(request);
    }
}
