package bioflix.catalog.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.Instant;
import java.util.Map;

@Document(collection = "showtimes")
@CompoundIndexes({
        @CompoundIndex(name = "movie_start", def = "{'movieId': 1, 'startsAt': 1}"),
        @CompoundIndex(name = "hall_start", def = "{'hallId': 1, 'startsAt': 1}")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Showtime {

    @Id
    private String id;

    /** Stored as a real ObjectId in Mongo, exposed as a String in Java. */
    @Field(name = "movieId", targetType = FieldType.OBJECT_ID)
    private String movieId;

    @Field(name = "hallId", targetType = FieldType.OBJECT_ID)
    private String hallId;

    /** Always UTC. Converted to Europe/Copenhagen only in the client. */
    @Field("startsAt")
    private Instant startsAt;

    @Field("endsAt")
    private Instant endsAt;

    @Field("format")
    private ShowFormat format;

    @Field("language")
    private String language;

    @Field("subtitles")
    private String subtitles;

    /** Price per seat type in oere, e.g. STANDARD -> 13500 = 135 kr. */
    @Field("prices")
    private Map<SeatType, Integer> prices;

    @Field("currency")
    private String currency;

    @Field("status")
    private ShowtimeStatus status;
}