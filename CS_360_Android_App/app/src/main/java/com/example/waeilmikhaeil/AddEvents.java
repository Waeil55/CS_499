package com.example.waeilmikhaeil;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEvents extends AppCompatActivity {
    private static final String TAG = "AddEvents";
    EditText eventTitle, eventDescription, eventTime, eventLocation;
    Button saveEventButton;
    CalendarView calendarView;
    EventsDatabase eventsDatabase;
    Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_events);

        eventTitle = findViewById(R.id.title);
        eventDescription = findViewById(R.id.description);
        eventTime = findViewById(R.id.time);
        eventLocation = findViewById(R.id.location);
        saveEventButton = findViewById(R.id.save);
        calendarView = findViewById(R.id.calendarView);

        eventsDatabase = new EventsDatabase(this);
        selectedDate = Calendar.getInstance();

        // Get the date from the intent
        int year = getIntent().getIntExtra("YEAR", -1);
        int month = getIntent().getIntExtra("MONTH", -1);
        int day = getIntent().getIntExtra("DAY", -1);

        if (year != -1 && month != -1 && day != -1) {
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, month);
            selectedDate.set(Calendar.DAY_OF_MONTH, day);
            calendarView.setDate(selectedDate.getTimeInMillis());
        }

        calendarView.setOnDateChangeListener((view, year1, month1, dayOfMonth) -> {
            selectedDate.set(Calendar.YEAR, year1);
            selectedDate.set(Calendar.MONTH, month1);
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        });

        saveEventButton.setOnClickListener(v -> saveEvent());
    }

    private void saveEvent() {
        String title = eventTitle.getText().toString().trim();
        String description = eventDescription.getText().toString().trim();
        String time = eventTime.getText().toString().trim();
        String location = eventLocation.getText().toString().trim();

        if (title.isEmpty() || time.isEmpty()) {
            Toast.makeText(AddEvents.this, "Title and Time are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Combine date and time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String[] timeParts = time.split(":");
        if (timeParts.length != 2) {
            Toast.makeText(AddEvents.this, "Invalid time format. Use HH:MM", Toast.LENGTH_SHORT).show();
            return;
        }
        selectedDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
        selectedDate.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
        String dateTime = sdf.format(selectedDate.getTime());

        Log.d(TAG, "Saving event: " + title + " on " + dateTime);

        Events event = new Events(title, description, dateTime, location);
        try {
            boolean saved = eventsDatabase.insertEvent(event);
            if (saved) {
                Toast.makeText(AddEvents.this, "Event Saved Successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(AddEvents.this, "Failed to save event", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving event", e);
            Toast.makeText(AddEvents.this, "Error saving event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}