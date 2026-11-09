package bioflix.catalog.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A cinema hall with a simple rectangular seat layout:
 * rows A, B, C ... and seats 1..seatsPerRow (seat label e.g. "C7").
 */
@Document(collection = "halls")
public class Hall {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private int rows;
    private int seatsPerRow;

    public Hall() {
    }

    public Hall(String name, int rows, int seatsPerRow) {
        this.name = name;
        this.rows = rows;
        this.seatsPerRow = seatsPerRow;
    }

    public int getCapacity() {
        return rows * seatsPerRow;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }
    public int getSeatsPerRow() { return seatsPerRow; }
    public void setSeatsPerRow(int seatsPerRow) { this.seatsPerRow = seatsPerRow; }
}
