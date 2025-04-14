package com.example.waeilmikhaeil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
    private Context context;
    private ArrayList<Events> eventsList;
    private OnEventInteractionListener listener;

    public interface OnEventInteractionListener {
        void onEventClick(Events event);
        void onEventLongClick(Events event);
    }

    public EventsAdapter(Context context, ArrayList<Events> eventsList, OnEventInteractionListener listener) {
        this.context = context;
        this.eventsList = eventsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Events event = eventsList.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView dateTextView;
        private TextView descriptionTextView;
        private TextView locationTextView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.event_title);
            dateTextView = itemView.findViewById(R.id.event_datetime);
            descriptionTextView = itemView.findViewById(R.id.event_description);
            locationTextView = itemView.findViewById(R.id.event_location);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEventClick(eventsList.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEventLongClick(eventsList.get(position));
                    return true;
                }
                return false;
            });
        }

        public void bind(Events event) {
            titleTextView.setText(event.getTitle());
            dateTextView.setText(event.getDateTime());
            descriptionTextView.setText(event.getDescription());
            locationTextView.setText(event.getLocation());
        }
    }
}