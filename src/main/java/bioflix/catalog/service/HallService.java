package bioflix.catalog.service;

import bioflix.catalog.dto.HallDtos.HallRequest;
import bioflix.catalog.dto.HallDtos.HallResponse;
import bioflix.catalog.entity.Hall;
import bioflix.catalog.exception.ConflictException;
import bioflix.catalog.exception.NotFoundException;
import bioflix.catalog.repository.HallRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HallService {

    private final HallRepository hallRepository;

    public HallService(HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }

    public List<HallResponse> getAll() {
        return hallRepository.findAll().stream().map(HallResponse::from).toList();
    }

    public HallResponse getById(String id) {
        return HallResponse.from(findOrThrow(id));
    }

    public HallResponse create(HallRequest request) {
        if (hallRepository.existsByName(request.name())) {
            throw new ConflictException("Hall '" + request.name() + "' already exists");
        }
        return HallResponse.from(hallRepository.save(new Hall(request.name(), request.rows(), request.seatsPerRow())));
    }

    public void delete(String id) {
        hallRepository.delete(findOrThrow(id));
    }

    public Hall findOrThrow(String id) {
        return hallRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Hall " + id + " not found"));
    }
}
