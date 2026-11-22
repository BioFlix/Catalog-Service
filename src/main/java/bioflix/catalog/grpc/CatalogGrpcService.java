package bioflix.catalog.grpc;

import bioflix.catalog.dto.HallResponse;
import bioflix.catalog.dto.ShowtimeDetails;
import bioflix.catalog.dto.ShowtimeResponse;
import bioflix.catalog.exception.NotFoundException;
import bioflix.catalog.service.ShowtimeService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Presentation layer for gRPC. Like the REST controllers, it only translates between the wire
 * format and the service layer. It holds no business logic.
 */
@Service
@RequiredArgsConstructor
public class CatalogGrpcService extends CatalogServiceGrpc.CatalogServiceImplBase {

    private final ShowtimeService showtimeService;

    @Override
    public void getShowtime(GetShowtimeRequest request,
                            StreamObserver<bioflix.catalog.grpc.ShowtimeResponse> observer) {
        try {
            ShowtimeDetails details = showtimeService.getDetails(request.getShowtimeId());
            observer.onNext(toProto(details));
            observer.onCompleted();
        } catch (NotFoundException e) {
            observer.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (IllegalArgumentException e) {
            observer.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (RuntimeException e) {
            observer.onError(Status.INTERNAL.withDescription("Could not load showtime").asRuntimeException());
        }
    }

    private bioflix.catalog.grpc.ShowtimeResponse toProto(ShowtimeDetails details) {
        ShowtimeResponse s = details.showtime();
        HallResponse hall = details.hall();

        Map<String, Integer> prices = s.prices().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue));

        bioflix.catalog.grpc.ShowtimeResponse.Builder builder =
                bioflix.catalog.grpc.ShowtimeResponse.newBuilder()
                        .setShowtimeId(s.id())
                        .setMovieId(s.movieId())
                        .setMovieTitle(s.movieTitle())
                        .setHallId(s.hallId())
                        .setHallName(s.hallName())
                        .setCinemaName(s.cinemaName())
                        .setStartsAt(s.startsAt().toString())
                        .setEndsAt(s.endsAt().toString())
                        .setFormat(s.format().name())
                        .setStatus(s.status().name())
                        .setCurrency(s.currency())
                        .putAllPrices(prices);

        hall.rows().forEach(row -> {
            SeatRow.Builder rowBuilder = SeatRow.newBuilder().setLabel(row.label());
            row.seats().forEach(seat -> rowBuilder.addSeats(
                    Seat.newBuilder().setNumber(seat.number()).setType(seat.type().name())));
            builder.addRows(rowBuilder);
        });

        return builder.build();
    }
}