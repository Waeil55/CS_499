# Databases Narrative

## Initial Artifact
My initial artifact was a simple SQLite database in my Android Event Management App. It stored events with columns for ID, title, description, date, and location. The database had basic functions to add, view, and delete events.

## Enhancements Made
I improved the database in these ways:
- **Indexing**: Added an index on the `date_time` column to make searches faster.
- **JOINs**: Created a `categories` table and used JOINs to connect events with categories, letting users filter events by category in the Search Events page.
- **Transactions**: Used transactions to ensure safe deletes, so deleting an event also removes its category links without errors.
- **Parameterized Queries**: Updated all database queries to use parameters, making the app safer from attacks like SQL injection.

## Skills Demonstrated
These changes show my skills in:
- Writing secure and efficient database code.
- Using advanced SQL features like JOINs and transactions.
- Testing to ensure the database works well with the app.

## Course Outcome Alignment
This work meets **outcome #3 (Databases)** by showing I can design and manage a database that is secure, fast, and reliable. My app now handles event data safely and efficiently.

## Files
- [EventsDatabase.java](EventsDatabase.java): The enhanced database code.