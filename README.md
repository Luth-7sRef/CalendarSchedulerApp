# Personal Calendar and Scheduler App (Java)

A console-based Java application designed to manage personal events, schedules, and recurring tasks. This project demonstrates Object-Oriented Programming (OOP) principles, custom File I/O (CSV-based persistence), and recurrence logic without the use of a database.

## Features

- **Event Management**: Create, Update, and Delete events.
- **Persistence**: All data is stored in local `.csv` files inside the `/data` folder.
- **Recurring Events**: Configure events to repeat daily or weekly for a set number of times or until a specific end date.
- **Calendar Views**: 
    - **Month View**: A visual CLI grid showing days of the month with event indicators (*).
    - **Week View**: A list view of all upcoming events for the next 7 days.
- **Search**: Find events by specific dates or date ranges.
- **Backup & Restore**: Export all data into a single `.zip` backup file and restore it to sync data across different PCs.

## 📁 Project Structure

```text
/MyCalendarApp
  |-- /src
       |-- Main.java             # User Interface and Menu Logic
       |-- CalendarManager.java  # Core business logic and recurrence calculation
       |-- FileHandler.java      # CSV operations and Zip Backup/Restore
       |-- Event.java            # Event data model
       |-- Recurrence.java       # Recurrence rule model
  |-- /data
       |-- event.csv             # Primary event storage
       |-- recurrent.csv         # Recurrence rules linked by eventId
