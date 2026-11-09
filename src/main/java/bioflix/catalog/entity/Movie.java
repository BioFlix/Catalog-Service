package bioflix.catalog.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "movies")
public class Movie {

    @Id
    private String id;

    @Indexed
    private String title;

    private String description;
    private int durationMinutes;
    private List<String> genres = new ArrayList<>();
    private String ageRating;      // e.g. "A", "7", "11", "15"
    private String posterUrl;      // S3 is "not yet wired" – just a URL for now

    public Movie() {
    }

    public Movie(String title, String description, int durationMinutes, List<String> genres,
                 String ageRating, String posterUrl) {
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.genres = genres;
        this.ageRating = ageRating;
        this.posterUrl = posterUrl;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }
    public String getAgeRating() { return ageRating; }
    public void setAgeRating(String ageRating) { this.ageRating = ageRating; }
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
}
