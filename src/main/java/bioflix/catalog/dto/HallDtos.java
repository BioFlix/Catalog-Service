package bioflix.catalog.dto;

import bioflix.catalog.entity.Hall;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public final class HallDtos {

    private HallDtos() {
    }

    public record HallRequest(
            @NotBlank String name,
            @Min(1) @Max(26) int rows,          // rows are labelled A..Z
            @Min(1) @Max(50) int seatsPerRow
    ) {
    }

    public record HallResponse(String id, String name, int rows, int seatsPerRow, int capacity) {
        public static HallResponse from(Hall hall) {
            return new HallResponse(hall.getId(), hall.getName(), hall.getRows(), hall.getSeatsPerRow(), hall.getCapacity());
        }
    }
}
