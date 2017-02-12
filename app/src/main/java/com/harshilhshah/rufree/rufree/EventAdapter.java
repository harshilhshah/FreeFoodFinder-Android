package com.harshilhshah.rufree.rufree;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.harshilhshah.rufree.rufree.Model.Event;
import com.harshilhshah.rufree.rufree.Utility.DownloadImageTask;
import com.harshilhshah.rufree.rufree.Utility.EventScraper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by harshilhshah on 1/11/16.
 */

public class EventAdapter extends
        RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> events;
    private DateComparator dc = new DateComparator();
    private Context mContext;

    EventAdapter(Context context, List<Event> foundEvents) {
        super();
        events = foundEvents;
        mContext = context;
        if(events != null)
            Collections.sort(events, dc);
        else
            events = new ArrayList<>();
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.event_item_cell, parent, false);


        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(EventAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Event item = events.get(position);

        ImageView imageView = viewHolder.imageView;
        Drawable drawable = ContextCompat.getDrawable(mContext, EventScraper.getRandomImage(item));
        DisplayMetrics display = mContext.getResources().getDisplayMetrics();
        new DownloadImageTask(imageView,drawable, display.widthPixels).execute(item.getImage_url());

        // Set item views based on your views and data model
        TextView nameView = viewHolder.nameView;
        TextView dateView = viewHolder.dateView;
        nameView.setText(item.getName());
        nameView.setTextColor(Color.BLACK);
        dateView.setText(item.getDateString());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return events.size();
    }

    Event getItem(int position){return events.get(position);}


    public void addItem(Event newEvent){
        Log.d("Change","Alert");
        events.add(0,newEvent);
        Collections.sort(events, dc);
        //notifyDataSetChanged();
        // Kevin made this change!
        notifyItemInserted(0);

    }

    private static class DateComparator implements Comparator<Event> {

        @Override
        public int compare(Event e1, Event e2) {
            if(e1 == null)
                return 1;
            if(e2 == null)
                return -1;

            if (e1.getStartGC().getTimeInMillis() > e2.getStartGC().getTimeInMillis())
                return 1;
            else
                return -1;
        }

    }


    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView nameView;
        TextView dateView;

        ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.image);
            nameView = (TextView) itemView.findViewById(R.id.event_item);
            dateView = (TextView) itemView.findViewById(R.id.date);
        }
    }
}


