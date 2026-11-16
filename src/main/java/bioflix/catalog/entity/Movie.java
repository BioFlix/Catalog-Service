package bioflix.catalog.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "movies")
@CompoundIndex(name = "status_release", def = "{'status': 1, 'releaseDate': -1}")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    private String id;

    @Field("title")
    private String title;

    @Indexed(unique = true)
    @Field("slug")
    private String slug;

    @Field("description")
    private String description;

    @Field("durationMinutes")
    private int durationMinutes;

    @Field("ageRating")
    private AgeRating ageRating;

    @Field("genres")
    private List<String> genres;

    @Field("originalLanguage")
    private String originalLanguage;

    @Field("releaseDate")
    private LocalDate releaseDate;

    @Field("posterUrl")
    private String posterUrl;

    @Field("status")
    private MovieStatus status;
}