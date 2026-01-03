import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event {
    private int id;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    // ISO format is convenient for sorting: 2025-10-05T11:00:00
    public static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    public static final DateTimeFormatter PRINT_FMT = DateTimeFormatter.ofPattern("MMM dd HH:mm");

    public Event(int id, String title, String description, LocalDateTime start, LocalDateTime end) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDateTime = start;
        this.endDateTime = end;
    }

    public String toCSV() {
        return id + "," + title + "," + description + "," + startDateTime.format(FMT) + "," + endDateTime.format(FMT);
    }

    public static Event fromCSV(String line) {
        String[] parts = line.split(",");
        if (parts.length < 5) return null;
        return new Event(
            Integer.parseInt(parts[0]),
            parts[1],
            parts[2],
            LocalDateTime.parse(parts[3], FMT),
            LocalDateTime.parse(parts[4], FMT)
        );
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public LocalDateTime getEndDateTime() { return endDateTime; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s (%s to %s)", 
            id, title, description, startDateTime.format(PRINT_FMT), endDateTime.format(PRINT_FMT));
    }
}