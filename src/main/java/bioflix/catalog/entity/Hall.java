package bioflix.catalog.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "halls")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hall {

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("cinema")
    private CinemaInfo cinema;

    @Field("capacity")
    private int capacity;

    @Field("rows")
    private List<SeatRow> rows;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CinemaInfo {
        @Field("name")
        private String name;

        @Field("city")
        private String city;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatRow {
        @Field("label")
        private String label;

        @Field("seats")
        private List<Seat> seats;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Seat {
        @Field("number")
        private int number;

        @Field("type")
        private SeatType type;
    }
}