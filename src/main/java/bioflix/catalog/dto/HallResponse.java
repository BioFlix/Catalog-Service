package bioflix.catalog.dto;

import bioflix.catalog.entity.SeatType;

import java.util.List;

public record HallResponse(
        String id,
        String name,
        String cinemaName,
        String city,
        int capacity,
        List<RowDto> rows
) {
    public record RowDto(String label, List<SeatDto> seats) {}

    public record SeatDto(int number, SeatType type) {}
}
