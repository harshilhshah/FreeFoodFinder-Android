package com.harshilhshah.rufree.rufree;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.harshilhshah.rufree.rufree.Model.Event;
import com.harshilhshah.rufree.rufree.Utility.DownloadImageTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by harshilhshah on 1/11/16.
 */
public class EventAdapter extends BaseAdapter {

    private List<Event> events;
    private DateComparator dc = new DateComparator();
    private Context context;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    EventAdapter(Context contxt, List<Event> foundEvents) {
        super();
        events = foundEvents;
        context = contxt;
        if(events != null)
            Collections.sort(events, dc);
        else
            events = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Event getItem(int position) {
        return  events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return  position;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater lf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final Event item = getItem(position);

        if (convertView == null)
            convertView = lf.inflate(R.layout.event_item_cell, parent, false);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.free_food_stamp);
        new DownloadImageTask(imageView,drawable).execute(item.getImage_url());

        TextView nameView = (TextView) convertView.findViewById(R.id.event_item);
        TextView dateView = (TextView) convertView.findViewById(R.id.date);

        nameView.setText(item.getName());
        nameView.setTextColor(Color.BLACK);
        dateView.setText(item.getDateString());

        return convertView;
    }

    public void addItem(Event newEvent){
        Log.d("Change","Alert");
        events.add(newEvent);
        Collections.sort(events, dc);
        notifyDataSetChanged();
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




}


