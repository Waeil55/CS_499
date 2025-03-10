package com.example.waeilmikhaeil;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditEvents extends AppCompatActivity {
    private static final String TAG = "EditEvents";
    private EditText eventTitle, eventDescription, eventDateTime, eventLocation;
    private Button updateEventButton;
    private EventsDatabase eventsDatabase;
    private int eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_events);

        initializeViews();
        initializeDatabase();
        getEventId();
        loadEventDetails();
        setupListeners();
    }

    private void initializeViews() {
        eventTitle = findViewById(R.id.title);
        eventDescription = findViewById(R.id.description);
        eventDateTime = findViewById(R.id.datetime);
        eventLocation = findViewById(R.id.location);
        updateEventButton = findViewById(R.id.update);
    }

    private void initializeDatabase() {
        eventsDatabase = new EventsDatabase(this);
    }

    private void getEventId() {
        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        if (eventId == -1) {
            Log.e(TAG, "No event ID provided");
            Toast.makeText(this, "Error: No event selected", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadEventDetails() {
        try {
            Events event = eventsDatabase.getEventById(eventId);
            if (event != null) {
                eventTitle.setText(event.getTitle());
                eventDescription.setText(event.getDescription());
                eventDateTime.setText(event.getDateTime());
                eventLocation.setText(event.getLocation());
            } else {
                Log.e(TAG, "Event not found for ID: " + eventId);
                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading event details", e);
            Toast.makeText(this, "Error loading event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupListeners() {
        updateEventButton.setOnClickListener(v -> updateEvent());
    }

    private void updateEvent() {
        String title = eventTitle.getText().toString().trim();
        String description = eventDescription.getText().toString().trim();
        String dateTime = eventDateTime.getText().toString().trim();
        String location = eventLocation.getText().toString().trim();

        if (title.isEmpty() || dateTime.isEmpty()) {
            Toast.makeText(this, "Title and Date/Time are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidDateTime(dateTime)) {
            Toast.makeText(this, "Invalid date/time format. Use yyyy-MM-dd HH:mm", Toast.LENGTH_SHORT).show();
            return;
        }

        Events updatedEvent = new Events(eventId, title, description, dateTime, location);
        try {
            boolean updated = eventsDatabase.updateEvent(updatedEvent);
            if (updated) {
                Toast.makeText(this, "Event Updated Successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update event", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating event", e);
            Toast.makeText(this, "Error updating event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidDateTime(String dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        sdf.setLenient(false);
        try {
            Date date = sdf.parse(dateTime);
            return date != null;
        } catch (ParseException e) {
            return false;
        }
    }
}