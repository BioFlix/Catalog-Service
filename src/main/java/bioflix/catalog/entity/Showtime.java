package bioflix.catalog.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "showtimes")
public class Showtime {

    @Id
    private String id;

    @Indexed
    private String movieId;

    private String hallId;

    @Indexed
    private Instant startTime;

    /** Price per seat in minor units (øre). Same unit Stripe uses. */
    private long priceMinor;

    private String currency = "DKK";

    public Showtime() {
    }

    public Showtime(String movieId, String hallId, Instant startTime, long priceMinor, String currency) {
        this.movieId = movieId;
        this.hallId = hallId;
        this.startTime = startTime;
        this.priceMinor = priceMinor;
        this.currency = currency;
    }

    public String getId() { return id; }
    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }
    public String getHallId() { return hallId; }
    public void setHallId(String hallId) { this.hallId = hallId; }
    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }
    public long getPriceMinor() { return priceMinor; }
    public void setPriceMinor(long priceMinor) { this.priceMinor = priceMinor; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
