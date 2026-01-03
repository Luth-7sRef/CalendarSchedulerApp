import java.time.*;
import java.util.*;

public class CalendarManager {
    private List<Event> events = new ArrayList<>();
    private List<Recurrence> recurrences = new ArrayList<>();
    private FileHandler fileHandler;

    public CalendarManager() {
        fileHandler = new FileHandler();
        load();
    }

    private void load() {
        events.clear();
        recurrences.clear();
        
        List<String> eventLines = fileHandler.readLines(FileHandler.EVENT_FILE);
        for (String line : eventLines) {
            if (line.startsWith("eventId")) continue; // Skip Header
            events.add(Event.fromCSV(line));
        }

        List<String> recurLines = fileHandler.readLines(FileHandler.RECUR_FILE);
        for (String line : recurLines) {
            if (line.startsWith("eventId")) continue;
            recurrences.add(Recurrence.fromCSV(line));
        }
    }

    public void save() {
        List<String> eLines = new ArrayList<>();
        eLines.add("eventId,title,description,startDateTime,endDateTime");
        for (Event e : events) eLines.add(e.toCSV());
        fileHandler.writeLines(FileHandler.EVENT_FILE, eLines);

        List<String> rLines = new ArrayList<>();
        rLines.add("eventId,recurrentInterval,recurrentTimes,recurrentEndDate");
        for (Recurrence r : recurrences) rLines.add(r.toCSV());
        fileHandler.writeLines(FileHandler.RECUR_FILE, rLines);
    }

    public void addEvent(String title, String desc, LocalDateTime start, LocalDateTime end, String recurInterval, int recurTimes, LocalDate recurEnd) {
        int newId = events.isEmpty() ? 1 : events.stream().mapToInt(Event::getId).max().orElse(0) + 1;
        Event e = new Event(newId, title, desc, start, end);
        events.add(e);

        if (!recurInterval.equals("none")) {
            recurrences.add(new Recurrence(newId, recurInterval, recurTimes, recurEnd));
        }
        save();
    }

    public boolean deleteEvent(int id) {
        boolean removed = events.removeIf(e -> e.getId() == id);
        recurrences.removeIf(r -> r.getEventId() == id);
        save();
        return removed;
    }

    // The Magic: Generate Recurring Instances for Viewing
    public List<Event> getEventsInWindow(LocalDateTime viewStart, LocalDateTime viewEnd) {
        List<Event> result = new ArrayList<>();

        for (Event e : events) {
            // Check original event
            if (isOverlap(e.getStartDateTime(), e.getEndDateTime(), viewStart, viewEnd)) {
                result.add(e);
            }

            // Check recurrence
            Recurrence r = recurrences.stream().filter(rec -> rec.getEventId() == e.getId()).findFirst().orElse(null);
            if (r != null) {
                result.addAll(generateRecurrences(e, r, viewStart, viewEnd));
            }
        }
        result.sort(Comparator.comparing(Event::getStartDateTime));
        return result;
    }

    private List<Event> generateRecurrences(Event e, Recurrence r, LocalDateTime viewStart, LocalDateTime viewEnd) {
        List<Event> generated = new ArrayList<>();
        LocalDateTime current = e.getStartDateTime();
        long durationMin = Duration.between(e.getStartDateTime(), e.getEndDateTime()).toMinutes();
        int count = 0;

        while (true) {
            // Advance date
            if (r.getInterval().equals("1d")) current = current.plusDays(1);
            else if (r.getInterval().equals("1w")) current = current.plusWeeks(1);
            else break; // Unknown interval

            count++;
            
            // Check stops
            if (r.getEndDate() != null && current.toLocalDate().isAfter(r.getEndDate())) break;
            if (r.getTimes() > 0 && count > r.getTimes()) break;
            if (current.isAfter(viewEnd)) break; // Optimization

            // Add if inside view window
            LocalDateTime currentEnd = current.plusMinutes(durationMin);
            if (isOverlap(current, currentEnd, viewStart, viewEnd)) {
                generated.add(new Event(e.getId(), e.getTitle() + " (R)", e.getDescription(), current, currentEnd));
            }
        }
        return generated;
    }

    private boolean isOverlap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
    
    public FileHandler getFileHandler() { return fileHandler; }
}