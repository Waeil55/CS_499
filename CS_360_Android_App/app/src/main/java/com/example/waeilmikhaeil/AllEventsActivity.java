package com.example.waeilmikhaeil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class AllEventsActivity extends BaseActivity implements EventsAdapter.OnEventInteractionListener {
    private static final String TAG = "AllEventsActivity";
    private RecyclerView activeEventsRecyclerView;
    private RecyclerView archivedEventsRecyclerView;
    private TextView noActiveEventsTextView;
    private TextView noArchivedEventsTextView;
    private EventsDatabase eventsDatabase;
    private EventsAdapter activeEventsAdapter;
    private EventsAdapter archivedEventsAdapter;
    private ArrayList<Events> activeEvents;
    private ArrayList<Events> archivedEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_events);

        activeEventsRecyclerView = findViewById(R.id.rv_active_events);
        archivedEventsRecyclerView = findViewById(R.id.rv_archived_events);
        noActiveEventsTextView = findViewById(R.id.tv_no_active_events);
        noArchivedEventsTextView = findViewById(R.id.tv_no_archived_events);
        eventsDatabase = new EventsDatabase(this);

        // Setup RecyclerViews
        activeEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        archivedEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activeEvents = new ArrayList<>();
        archivedEvents = new ArrayList<>();
        activeEventsAdapter = new EventsAdapter(this, activeEvents, this);
        archivedEventsAdapter = new EventsAdapter(this, archivedEvents, this);
        activeEventsRecyclerView.setAdapter(activeEventsAdapter);
        archivedEventsRecyclerView.setAdapter(archivedEventsAdapter);

        loadEvents();

        // Bottom Navigation View
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_view);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_add) {
                startActivity(new Intent(this, AddEvents.class));
                return true;
            } else if (itemId == R.id.nav_view) {
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

    private void loadEvents() {
        try {
            activeEvents.clear();
            activeEvents.addAll(eventsDatabase.viewEvents());
            activeEventsAdapter.notifyDataSetChanged();
            noActiveEventsTextView.setVisibility(activeEvents.isEmpty() ? View.VISIBLE : View.GONE);

            archivedEvents.clear();
            archivedEvents.addAll(eventsDatabase.viewArchivedEvents());
            archivedEventsAdapter.notifyDataSetChanged();
            noArchivedEventsTextView.setVisibility(archivedEvents.isEmpty() ? View.VISIBLE : View.GONE);

            if (activeEvents.isEmpty() && archivedEvents.isEmpty()) {
                Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to load events", e);
            Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEventClick(Events event) {
        Intent intent = new Intent(this, EditEvents.class);
        intent.putExtra("EVENT_ID", event.getId());
        startActivity(intent);
    }

    @Override
    public void onEventLongClick(Events event) {
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
                activeEvents.remove(event);
                archivedEvents.remove(event);
                activeEventsAdapter.notifyDataSetChanged();
                archivedEventsAdapter.notifyDataSetChanged();
                noActiveEventsTextView.setVisibility(activeEvents.isEmpty() ? View.VISIBLE : View.GONE);
                noArchivedEventsTextView.setVisibility(archivedEvents.isEmpty() ? View.VISIBLE : View.GONE);
                Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting event", e);
            Toast.makeText(this, "Error deleting event", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }
}