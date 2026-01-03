import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static CalendarManager manager = new CalendarManager();
    private static final DateTimeFormatter INPUT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        System.out.println("=== Personal Calendar App ===");
        while (true) {
            System.out.println("\n1. Create Event\n2. View Calendar (Month)\n3. View List (Week)\n4. Search/Filter\n5. Delete Event\n6. Backup\n7. Restore\n0. Exit");
            System.out.print("Choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1": createEventUI(); break;
                case "2": viewMonthUI(); break;
                case "3": viewWeekUI(); break;
                case "4": searchUI(); break;
                case "5": deleteUI(); break;
                case "6": 
                    System.out.print("Enter backup filename (e.g. backup.zip): ");
                    manager.getFileHandler().backup(scanner.nextLine());
                    break;
                case "7":
                    System.out.print("Enter backup filename to restore: ");
                    manager.getFileHandler().restore(scanner.nextLine());
                    // Reload data from restored files
                    manager = new CalendarManager(); 
                    break;
                case "0": System.exit(0);
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private static void createEventUI() {
        try {
            System.out.print("Title: "); String title = scanner.nextLine();
            System.out.print("Desc: "); String desc = scanner.nextLine();
            System.out.print("Start (yyyy-MM-dd HH:mm): "); LocalDateTime start = LocalDateTime.parse(scanner.nextLine(), INPUT_FMT);
            System.out.print("End (yyyy-MM-dd HH:mm): "); LocalDateTime end = LocalDateTime.parse(scanner.nextLine(), INPUT_FMT);
            
            System.out.print("Recurrence (none, 1d, 1w): "); String recur = scanner.nextLine();
            int times = 0;
            LocalDate recurEnd = null;
            
            if (!recur.equals("none")) {
                System.out.print("Stop after X times (0 for date): ");
                times = Integer.parseInt(scanner.nextLine());
                if (times == 0) {
                    System.out.print("Stop date (yyyy-MM-dd): ");
                    recurEnd = LocalDate.parse(scanner.nextLine());
                }
            }
            
            manager.addEvent(title, desc, start, end, recur, times, recurEnd);
            System.out.println("Event created!");
        } catch (Exception e) {
            System.out.println("Error reading input. Please follow format.");
        }
    }

    private static void viewMonthUI() {
        System.out.print("Enter Year (e.g. 2025): "); int year = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Month (1-12): "); int month = Integer.parseInt(scanner.nextLine());
        
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59);
        
        List<Event> events = manager.getEventsInWindow(start, end);
        
        System.out.println("\n   " + ym.getMonth() + " " + year);
        System.out.println("Su Mo Tu We Th Fr Sa");
        
        int startDay = ym.atDay(1).getDayOfWeek().getValue() % 7; // Sun=0
        for (int i = 0; i < startDay; i++) System.out.print("   ");
        
        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            boolean hasEvent = false;
            for (Event e : events) {
                if (e.getStartDateTime().getDayOfMonth() == day) hasEvent = true;
            }
            
            if (hasEvent) System.out.printf("%2d*", day);
            else System.out.printf("%2d ", day);
            
            if ((day + startDay) % 7 == 0) System.out.println();
        }
        System.out.println("\n");
        // Print List below calendar
        for(Event e : events) System.out.println(e);
    }

    private static void viewWeekUI() {
        // Simple logic: View next 7 days from today
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(7);
        List<Event> events = manager.getEventsInWindow(start, end);
        System.out.println("=== Events for next 7 days ===");
        for(Event e : events) System.out.println(e);
    }
    
    private static void searchUI() {
        System.out.print("Search Date (yyyy-MM-dd): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());
        List<Event> events = manager.getEventsInWindow(date.atStartOfDay(), date.atTime(23, 59));
        if(events.isEmpty()) System.out.println("No events found.");
        else for(Event e : events) System.out.println(e);
    }

    private static void deleteUI() {
        System.out.print("Enter Event ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());
        if(manager.deleteEvent(id)) System.out.println("Deleted.");
        else System.out.println("ID not found.");
    }
}