package com.example.waeilmikhaeil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

public class EventsAdapter extends ArrayAdapter<Events> {

    public EventsAdapter(@NonNull Context context, ArrayList<Events> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Events event = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_item, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.event_title);
        TextView descriptionTextView = convertView.findViewById(R.id.event_description);
        TextView dateTimeTextView = convertView.findViewById(R.id.event_datetime);
        TextView locationTextView = convertView.findViewById(R.id.event_location);

        titleTextView.setText(event.getTitle());
        descriptionTextView.setText(event.getDescription());
        dateTimeTextView.setText(event.getDateTime());
        locationTextView.setText(event.getLocation());

        return convertView;
    }
}