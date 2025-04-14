package com.example.waeilmikhaeil;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditEvents extends BaseActivity {
    private static final String TAG = "EditEvents";
    private TextInputEditText eventTitle, eventDescription, eventDateTime, eventLocation;
    private MaterialButton updateButton, archiveButton;
    private EventsDatabase eventsDatabase;
    private int eventId;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_events);

        eventTitle = findViewById(R.id.title);
        eventDescription = findViewById(R.id.description);
        eventDateTime = findViewById(R.id.datetime);
        eventLocation = findViewById(R.id.location);
        updateButton = findViewById(R.id.update);
        archiveButton = findViewById(R.id.btn_archive);

        eventsDatabase = new EventsDatabase(this);
        selectedDate = Calendar.getInstance();

        // Get the event ID from the intent
        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        if (eventId == -1) {
            Toast.makeText(this, "Invalid event ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load the event details
        Events event = eventsDatabase.getEventById(eventId);
        if (event != null) {
            eventTitle.setText(event.getTitle());
            eventDescription.setText(event.getDescription());
            eventDateTime.setText(event.getDateTime());
            eventLocation.setText(event.getLocation());
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                selectedDate.setTime(sdf.parse(event.getDateTime()));
            } catch (Exception e) {
                Log.e(TAG, "Error parsing event date", e);
            }
        } else {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Show TimePicker on clicking datetime field
        eventDateTime.setOnClickListener(v -> showTimePicker());

        updateButton.setOnClickListener(v -> updateEvent());
        archiveButton.setOnClickListener(v -> archiveEvent());

        // Bottom Navigation View
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_add) {
                startActivity(new Intent(this, AddEvents.class));
                return true;
            } else if (itemId == R.id.nav_view) {
                startActivity(new Intent(this, AllEventsActivity.class));
                return true;
            } else if (itemId == R.id.nav_search) {
                startActivity(new Intent(this, SearchEvents.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            } else if (itemId == R.id.nav_help) {
                startActivity(new Intent(this, HelpActivity.class));
                return true;
            }
            return false;
        });
    }

    private void showTimePicker() {
        int hour = selectedDate.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDate.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDate.set(Calendar.MINUTE, minute1);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    eventDateTime.setText(sdf.format(selectedDate.getTime()));
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void updateEvent() {
        String title = eventTitle.getText() != null ? eventTitle.getText().toString().trim() : "";
        String description = eventDescription.getText() != null ? eventDescription.getText().toString().trim() : "";
        String dateTime = eventDateTime.getText() != null ? eventDateTime.getText().toString().trim() : "";
        String location = eventLocation.getText() != null ? eventLocation.getText().toString().trim() : "";

        if (title.isEmpty() || dateTime.isEmpty()) {
            Toast.makeText(this, "Title and Date/Time are required", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        sdf.setLenient(false);
        try {
            sdf.parse(dateTime);
        } catch (Exception e) {
            Log.e(TAG, "Invalid date/time format", e);
            Toast.makeText(this, "Invalid format. Use yyyy-MM-dd HH:mm", Toast.LENGTH_SHORT).show();
            return;
        }

        Events updatedEvent = new Events(eventId, title, description, dateTime, location);
        if (eventsDatabase.updateEvent(updatedEvent)) {
            Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update event", Toast.LENGTH_SHORT).show();
        }
    }

    private void archiveEvent() {
        if (eventsDatabase.archiveEvent(eventId)) {
            Toast.makeText(this, "Event archived successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to archive event", Toast.LENGTH_SHORT).show();
        }
    }
}