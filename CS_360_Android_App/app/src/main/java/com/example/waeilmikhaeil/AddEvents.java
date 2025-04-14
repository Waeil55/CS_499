package com.example.waeilmikhaeil;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEvents extends BaseActivity {
    private static final String TAG = "AddEvents";
    private TextInputEditText eventTitle, eventDescription, eventDateTime, eventLocation;
    private Button saveEventButton;
    private CalendarView calendarView;
    private EventsDatabase eventsDatabase;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_events);

        eventTitle = findViewById(R.id.title);
        eventDescription = findViewById(R.id.description);
        eventDateTime = findViewById(R.id.datetime);
        eventLocation = findViewById(R.id.location);
        saveEventButton = findViewById(R.id.save);
        calendarView = findViewById(R.id.calendarView);

        eventsDatabase = new EventsDatabase(this);
        selectedDate = Calendar.getInstance();

        // Check if a date was passed via intent
        int year = getIntent().getIntExtra("YEAR", -1);
        int month = getIntent().getIntExtra("MONTH", -1);
        int day = getIntent().getIntExtra("DAY", -1);

        if (year != -1 && month != -1 && day != -1) {
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, month);
            selectedDate.set(Calendar.DAY_OF_MONTH, day);
            calendarView.setDate(selectedDate.getTimeInMillis());
            updateDateTimeField();
        }

        // Update the datetime field when a date is selected
        calendarView.setOnDateChangeListener((view, year1, month1, dayOfMonth) -> {
            selectedDate.set(Calendar.YEAR, year1);
            selectedDate.set(Calendar.MONTH, month1);
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateTimeField();
        });

        // Show TimePicker on clicking datetime field
        eventDateTime.setOnClickListener(v -> showTimePicker());

        saveEventButton.setOnClickListener(v -> saveEvent());

        // Bottom Navigation View
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_add);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_add) {
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

    private void updateDateTimeField() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        eventDateTime.setText(sdf.format(selectedDate.getTime()));
    }

    private void showTimePicker() {
        int hour = selectedDate.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDate.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDate.set(Calendar.MINUTE, minute1);
                    updateDateTimeField();
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void saveEvent() {
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

        Events event = new Events(title, description, dateTime, location);
        if (eventsDatabase.insertEvent(event)) {
            Log.d(TAG, "Event saved: " + title);
            Toast.makeText(this, "Event Saved Successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Log.e(TAG, "Failed to save event");
            Toast.makeText(this, "Failed to save event", Toast.LENGTH_SHORT).show();
        }
    }
}