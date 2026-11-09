package bioflix.catalog.controller;

import bioflix.catalog.dto.HallDtos.HallRequest;
import bioflix.catalog.dto.HallDtos.HallResponse;
import bioflix.catalog.service.HallService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/halls")
public class HallController {

    private final HallService hallService;

    public HallController(HallService hallService) {
        this.hallService = hallService;
    }

    @GetMapping
    public List<HallResponse> getAll() {
        return hallService.getAll();
    }

    @GetMapping("/{id}")
    public HallResponse getById(@PathVariable String id) {
        return hallService.getById(id);
    }

    // TODO: admin only
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HallResponse create(@Valid @RequestBody HallRequest request) {
        return hallService.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        hallService.delete(id);
    }
}
