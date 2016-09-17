package com.harshilhshah.rufree.rufree;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.harshilhshah.rufree.rufree.Model.Event;
import com.harshilhshah.rufree.rufree.Utility.DownloadImageTask;

public class EventDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detailed_display);
        Event event = this.getIntent().getParcelableExtra("Event");
        ImageView imageView = (ImageView) findViewById(R.id.event_cover_picture);
        Drawable drawable = ContextCompat.getDrawable(this.getApplicationContext(), R.drawable.free_food_stamp);
        new DownloadImageTask(imageView,drawable).execute(event.getImage_url());
        TextView timeView = (TextView) findViewById(R.id.event_time);
        TextView nameView = (TextView) findViewById(R.id.event_name);
        TextView descView = (TextView) findViewById(R.id.event_description);
        timeView.setText(event.getTimeString());
        timeView.setTextColor(Color.BLACK);
        nameView.setText(event.getName());
        descView.setText(event.getDescription());
    }
}
