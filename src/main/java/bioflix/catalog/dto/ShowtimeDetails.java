package bioflix.catalog.dto;

/**
 * Everything Booking needs in one call: the showtime plus its movie title and the hall's seat layout.
 * Used by the gRPC endpoint.
 */
public record ShowtimeDetails(ShowtimeResponse showtime, HallResponse hall) {}
