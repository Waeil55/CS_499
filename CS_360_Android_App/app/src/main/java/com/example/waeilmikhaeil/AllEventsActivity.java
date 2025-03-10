package com.example.waeilmikhaeil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import java.util.ArrayList;

public class AllEventsActivity extends AppCompatActivity {
    private static final String TAG = "AllEventsActivity";
    private ListView listView;
    private EventsDatabase eventsDatabase;
    private EventsAdapter adapter;
    private ArrayList<Events> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_events);

        listView = findViewById(R.id.list_view_all_events);
        eventsDatabase = new EventsDatabase(this);

        loadAllEvents();
        setupListeners();
    }

    private void loadAllEvents() {
        try {
            events = eventsDatabase.viewEvents();
            if (events.isEmpty()) {
                Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show();
            }
            adapter = new EventsAdapter(this, events);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "Failed to load events", e);
            Toast.makeText(this, "Failed to load events: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
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

    private void launchEditEventActivity(int eventId) {
        Intent intent = new Intent(AllEventsActivity.this, EditEvents.class);
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

    @Override
    protected void onResume() {
        super.onResume();
        loadAllEvents();
    }
}