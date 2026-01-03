import java.time.LocalDate;

public class Recurrence {
    private int eventId;
    private String interval; // "1d", "1w"
    private int times;
    private LocalDate endDate;

    public Recurrence(int eventId, String interval, int times, LocalDate endDate) {
        this.eventId = eventId;
        this.interval = interval;
        this.times = times;
        this.endDate = endDate;
    }

    public String toCSV() {
        String dateStr = (endDate == null) ? "0" : endDate.toString();
        return eventId + "," + interval + "," + times + "," + dateStr;
    }

    public static Recurrence fromCSV(String line) {
        String[] parts = line.split(",");
        if (parts.length < 4) return null;
        
        LocalDate end = parts[3].equals("0") ? null : LocalDate.parse(parts[3]);
        return new Recurrence(
            Integer.parseInt(parts[0]),
            parts[1],
            Integer.parseInt(parts[2]),
            end
        );
    }

    public int getEventId() { return eventId; }
    public String getInterval() { return interval; }
    public int getTimes() { return times; }
    public LocalDate getEndDate() { return endDate; }
}