package com.harshilhshah.rufree.rufree;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

import com.harshilhshah.rufree.rufree.Model.Event;
import com.harshilhshah.rufree.rufree.Utility.DownloadImageTask;
import com.harshilhshah.rufree.rufree.Utility.EventScraper;

public class EventDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detailed_display);
        Event event = this.getIntent().getParcelableExtra("Event");
        ImageView imageView = (ImageView) findViewById(R.id.event_cover_picture);
        Drawable drawable = ContextCompat.getDrawable(this.getApplicationContext(), EventScraper.getRandomImage(event));
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        new DownloadImageTask(imageView,drawable,size.x).execute(event.getImage_url());
        TextView timeView = (TextView) findViewById(R.id.event_time);
        TextView nameView = (TextView) findViewById(R.id.event_name);
        TextView locationView = (TextView) findViewById(R.id.event_location);
        TextView descView = (TextView) findViewById(R.id.event_description);
        TextView tagView = (TextView) findViewById(R.id.event_tags);
        timeView.setText(event.getTimeString());
        timeView.setTextColor(Color.BLACK);
        nameView.setText(event.getName());
        locationView.setText(event.getCampus_loc().toString().replace(":",", "));
        tagView.setText(event.getTags());
        descView.setText(event.getDescription());
    }
}
