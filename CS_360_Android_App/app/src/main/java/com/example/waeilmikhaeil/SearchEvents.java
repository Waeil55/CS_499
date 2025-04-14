package com.example.waeilmikhaeil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class SearchEvents extends AppCompatActivity implements EventsAdapter.OnEventInteractionListener {
    private RecyclerView recyclerView;
    private EventsAdapter eventsAdapter;
    private ArrayList<Events> eventsList;
    private EventsDatabase eventsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_events);

        recyclerView = findViewById(R.id.rv_search_events);
        SearchView searchView = findViewById(R.id.sv_search_events);
        eventsDatabase = new EventsDatabase(this);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsList = new ArrayList<>();
        eventsAdapter = new EventsAdapter(this, eventsList, this);
        recyclerView.setAdapter(eventsAdapter);

        // Load all active events initially
        loadEvents();

        // Set up SearchView to filter events
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterEvents(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterEvents(newText);
                return true;
            }
        });

        // Bottom Navigation View
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_search);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_add) {
                startActivity(new Intent(this, AddEvents.class));
                return true;
            } else if (itemId == R.id.nav_view) {
                startActivity(new Intent(this, AllEventsActivity.class));
                return true;
            } else if (itemId == R.id.nav_search) {
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
        eventsList.clear();
        eventsList.addAll(eventsDatabase.viewEvents());
        eventsAdapter.notifyDataSetChanged();
    }

    private void filterEvents(String query) {
        ArrayList<Events> filteredList = new ArrayList<>();
        for (Events event : eventsDatabase.viewEvents()) {
            if (event.getTitle() != null && event.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(event);
            }
        }
        eventsList.clear();
        eventsList.addAll(filteredList);
        eventsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEventClick(Events event) {
        Intent intent = new Intent(this, EditEvents.class);
        intent.putExtra("EVENT_ID", event.getId());
        startActivity(intent);
    }

    @Override
    public void onEventLongClick(Events event) {
        // Optional: Add long-click behavior if needed
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }
}