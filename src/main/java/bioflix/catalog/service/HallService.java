package bioflix.catalog.service;

import bioflix.catalog.dto.HallRequest;
import bioflix.catalog.dto.HallResponse;
import bioflix.catalog.entity.Hall;
import bioflix.catalog.exception.ConflictException;
import bioflix.catalog.exception.NotFoundException;
import bioflix.catalog.mapper.CatalogMapper;
import bioflix.catalog.repository.HallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class HallService {

    private final HallRepository halls;
    private final CatalogMapper mapper;

    public List<HallResponse> list(String city) {
        List<Hall> result = city == null ? halls.findAll() : halls.findByCinemaCity(city);
        return result.stream().map(mapper::toResponse).toList();
    }

    public HallResponse get(String id) {
        return mapper.toResponse(find(id));
    }

    public HallResponse create(HallRequest request) {
        return mapper.toResponse(halls.save(mapper.toEntity(request)));
    }

    Hall find(String id) {
        return halls.findById(id).orElseThrow(() -> new NotFoundException("Hall", id));
    }

    private void validateSeats(List<HallRequest.RowDto> rows) {
        Set<String> labels = new HashSet<>();
        for (HallRequest.RowDto row : rows) {
            if (!labels.add(row.label())) {
                throw new ConflictException("Duplicate row label: " + row.label());
            }
            Set<Integer> numbers = new HashSet<>();
            for (HallRequest.SeatDto seat : row.seats()) {
                if (!numbers.add(seat.number())) {
                    throw new ConflictException("Duplicate seat " + row.label() + seat.number());
                }
            }
        }
    }
}
