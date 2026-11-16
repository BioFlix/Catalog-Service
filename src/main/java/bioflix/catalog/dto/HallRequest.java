package bioflix.catalog.dto;

import bioflix.catalog.entity.SeatType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record HallRequest(
        @NotBlank String name,
        @NotBlank String cinemaName,
        @NotBlank String city,
        @NotEmpty @Valid List<RowDto> rows
) {
    public record RowDto(@NotBlank String label, @NotEmpty @Valid List<SeatDto> seats) {}

    public record SeatDto(int number, @NotNull SeatType type) {}
}
