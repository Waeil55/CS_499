package com.example.waeilmikhaeil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class ViewEvents extends BaseActivity implements EventsAdapter.OnEventInteractionListener {
    private RecyclerView recyclerView;
    private Button addEventButton;
    private EventsAdapter eventsAdapter;
    private ArrayList<Events> eventsList;
    private EventsDatabase eventsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_events);

        recyclerView = findViewById(R.id.recycler_view);
        addEventButton = findViewById(R.id.btn_add_event);
        eventsDatabase = new EventsDatabase(this);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsList = eventsDatabase.viewEvents();
        eventsAdapter = new EventsAdapter(this, eventsList, this);
        recyclerView.setAdapter(eventsAdapter);

        // Set up button to navigate to AddEvents
        addEventButton.setOnClickListener(v -> startActivity(new Intent(this, AddEvents.class)));

        // Bottom Navigation View
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_view);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_add) {
                startActivity(new Intent(this, AddEvents.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_view) {
                return true;
            } else if (itemId == R.id.nav_search) {
                startActivity(new Intent(this, SearchEvents.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    public void onEventClick(Events event) {
        Intent intent = new Intent(this, EditEvents.class);
        intent.putExtra("EVENT_ID", event.getId());
        startActivity(intent);
    }

    @Override
    public void onEventLongClick(Events event) {
        // Optional: Add long-click functionality if needed, similar to AllEventsActivity
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning to this activity
        eventsList.clear();
        eventsList.addAll(eventsDatabase.viewEvents());
        eventsAdapter.notifyDataSetChanged();
    }
}