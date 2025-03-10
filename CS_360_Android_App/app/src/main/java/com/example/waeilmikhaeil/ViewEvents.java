package com.example.waeilmikhaeil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewEvents extends AppCompatActivity {
    private static final String TAG = "ViewEvents";
    private ListView listView;
    private CalendarView calendarView;
    private EventsDatabase eventsDatabase;
    private EventsAdapter adapter;
    private ArrayList<Events> events;
    private FloatingActionButton fabAddEvent;
    private FloatingActionButton fabViewAllEvents;
    private FloatingActionButton fabSettings;
    private SmsNotificationManager smsNotificationManager;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_events);

        initializeViews();
        initializeDatabase();
        setupListeners();
        initializeSmsNotificationManager();
        loadEvents();
    }

    private void initializeViews() {
        listView = findViewById(R.id.list_view);
        calendarView = findViewById(R.id.calendarView);
        fabAddEvent = findViewById(R.id.fab_add_event);
        fabViewAllEvents = findViewById(R.id.fab_view_all_events);
        fabSettings = findViewById(R.id.fab_settings);
    }

    private void initializeDatabase() {
        try {
            eventsDatabase = new EventsDatabase(this);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database", e);
            Toast.makeText(this, "Failed to initialize database: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeSmsNotificationManager() {
        smsNotificationManager = new SmsNotificationManager(this);
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
    }

    private void setupListeners() {
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            launchAddEventActivity(year, month, dayOfMonth);
        });

        fabAddEvent.setOnClickListener(v -> launchAddEventActivity());
        fabViewAllEvents.setOnClickListener(v -> launchViewAllEventsActivity());
        fabSettings.setOnClickListener(v -> launchSettingsActivity());

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Events selectedEvent = events.get(position);
            launchEditEventActivity(selectedEvent.getId());
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Events selectedEvent = events.get(position);
            showDeleteConfirmationDialog(selectedEvent);
            return true;
        });
    }

    private void launchAddEventActivity(int year, int month, int dayOfMonth) {
        Intent intent = new Intent(ViewEvents.this, AddEvents.class);
        intent.putExtra("YEAR", year);
        intent.putExtra("MONTH", month);
        intent.putExtra("DAY", dayOfMonth);
        startActivity(intent);
    }

    private void launchAddEventActivity() {
        Intent intent = new Intent(ViewEvents.this, AddEvents.class);
        startActivity(intent);
    }

    private void launchViewAllEventsActivity() {
        Intent intent = new Intent(ViewEvents.this, AllEventsActivity.class);
        startActivity(intent);
    }

    private void launchSettingsActivity() {
        Intent intent = new Intent(ViewEvents.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void launchEditEventActivity(int eventId) {
        Intent intent = new Intent(ViewEvents.this, EditEvents.class);
        intent.putExtra("EVENT_ID", eventId);
        startActivity(intent);
    }

    private void showDeleteConfirmationDialog(Events event) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Yes", (dialog, which) -> deleteEvent(event))
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    private void loadEvents() {
        try {
            events = eventsDatabase.viewEvents();
            if (events.isEmpty()) {
                Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show();
            }
            adapter = new EventsAdapter(this, events);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            checkUpcomingEvents();
        } catch (Exception e) {
            Log.e(TAG, "Failed to load events", e);
            Toast.makeText(this, "Failed to load events: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteEvent(Events event) {
        try {
            if (eventsDatabase.deleteEvent(event.getId())) {
                events.remove(event);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting event", e);
            Toast.makeText(this, "Error deleting event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUpcomingEvents() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        boolean smsNotificationsEnabled = sharedPreferences.getBoolean("SMS_NOTIFICATIONS_ENABLED", false);

        for (Events event : events) {
            try {
                Date eventDate = sdf.parse(event.getDateTime());
                if (eventDate != null) {
                    calendar.setTime(eventDate);
                    calendar.add(Calendar.HOUR, -24); // 24 hours before the event
                    Date notificationTime = calendar.getTime();

                    if (now.after(notificationTime) && now.before(eventDate) && smsNotificationsEnabled) {
                        String message = "Upcoming event: " + event.getTitle() + " at " + event.getDateTime();
                        smsNotificationManager.sendEventNotification("1234567890", message);
                    }
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date for event: " + event.getTitle(), e);
            }
        }
    }
}