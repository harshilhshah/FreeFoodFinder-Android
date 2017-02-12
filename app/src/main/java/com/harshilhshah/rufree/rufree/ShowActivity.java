package com.harshilhshah.rufree.rufree;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.appevents.AppEventsLogger;
import com.harshilhshah.rufree.rufree.Model.Event;
import com.harshilhshah.rufree.rufree.Utility.EventScraper;
import com.harshilhshah.rufree.rufree.Utility.ItemClickSupport;


public class ShowActivity extends AppCompatActivity {

    private final EventAdapter eventAdapter = new EventAdapter(this,null);

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

         RecyclerView rvContacts = (RecyclerView) findViewById(R.id.event_list);
        rvContacts.setAdapter(eventAdapter);
         rvContacts.setLayoutManager(new LinearLayoutManager(this));

         ItemClickSupport.addTo(rvContacts).setOnItemClickListener(
                 new ItemClickSupport.OnItemClickListener() {
                     @Override
                     public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                         Event event = eventAdapter.getItem(position);
                         Intent intent = new Intent(ShowActivity.this, EventDisplayActivity.class);
                         intent.putExtra("Event",event);
                         startActivity(intent);
                         eventAdapter.notifyDataSetChanged();
                     }
                 }
         );

         /*FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
         myFab.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 Intent i = new Intent(ShowActivity.this,CreateEventActivity.class);
                 startActivity(i);
                 eventAdapter.notifyDataSetChanged();
             }
         });*/


        new EventScraper(eventAdapter);
    }

     @Override
     protected void onResume() {
         super.onResume();

         // Logs 'install' and 'app activate' App Events.
         AppEventsLogger.activateApp(this);
     }

     @Override
     protected void onPause() {
         super.onPause();

         // Logs 'app deactivate' App Event.
         AppEventsLogger.deactivateApp(this);
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



 }
