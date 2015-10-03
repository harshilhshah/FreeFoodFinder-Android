package com.harshilhshah.rufree.rufree;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Intent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.harshilhshah.rufree.rufree.Model.Event;
import com.harshilhshah.rufree.rufree.Utility.EventScraper;

import com.facebook.FacebookSdk;
import com.facebook.FacebookException;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.CallbackManager;
import com.facebook.appevents.AppEventsLogger;


 public class MainActivity extends ActionBarActivity {

    private ListView event_list;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_events_display);

        event_list = (ListView)findViewById(R.id.event_list);
         LoginButton loginbutton = (LoginButton) findViewById(R.id.login_button);

         loginbutton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
             @Override
             public void onSuccess(LoginResult loginResult) {
                 Log.d("FB Success", loginResult.getAccessToken().getUserId());
                 new GetEventsTask(true).execute();
             }

             @Override
             public void onCancel() {
                 Log.d("NO FB Access", "User doesn't want to grant access");
             }

             @Override
             public void onError(FacebookException e) {
                 Log.e("Facebook Error", e.toString());
             }
         });

         accessTokenTracker = new AccessTokenTracker() {
             @Override
             protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {

             }
         };


         if(AccessToken.getCurrentAccessToken() != null){
             new GetEventsTask(true).execute();
         }else{
             new GetEventsTask(false).execute();
         }

    }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         callbackManager.onActivityResult(requestCode, resultCode, data);
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
     public void onDestroy() {
         super.onDestroy();
         accessTokenTracker.stopTracking();
     }

     private class GetEventsTask extends AsyncTask<Void,Void,ArrayList<Event>> {


         private ProgressDialog progressDialog;
         private boolean getFBEvents;

         public GetEventsTask(boolean bool) {
             super();
             getFBEvents = bool;
         }

         @Override
         protected void onPreExecute() {
             super.onPreExecute();

             progressDialog = new ProgressDialog(MainActivity.this);

             progressDialog.setTitle("Waiting...");

             progressDialog.setIndeterminate(true);
         }


         @Override
         protected ArrayList<Event> doInBackground(Void... params) {

             ArrayList<Event> events = null;

             try {
                 events = EventScraper.getEvents(getFBEvents);
             }
             catch (IOException e){
                 Log.e("IOException", e.getMessage());
             }

             return events;
         }



         @Override
         protected void onPostExecute(ArrayList<Event> events) {
             super.onPostExecute(events);

             progressDialog.dismiss();
             event_list.setBackgroundColor(Color.WHITE);
             final EventAdapter eventAdapter = new EventAdapter(events);

             event_list.setAdapter(eventAdapter);
             event_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                 @Override
                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                     eventAdapter.selectedItem(position);
                     eventAdapter.notifyDataSetChanged();
                 }
             });
         }
     }

     private class EventAdapter extends BaseAdapter {

         private List<Event> events;
         private int position;

         public EventAdapter(List<Event> foundEvents) {
             super();
             events = foundEvents;
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

             LayoutInflater lf = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

             Event item = getItem(position);

             if(convertView == null){
                 convertView = lf.inflate(R.layout.event_item_cell, parent, false);
             }

             LinearLayout ll = (LinearLayout)convertView.findViewById(R.id.event_ll);
             TextView nameView = (TextView)convertView.findViewById(R.id.event_item);
             TextView dateView = (TextView)convertView.findViewById(R.id.date);
             TextView info = (TextView)convertView.findViewById(R.id.event_item_selected);

             nameView.setText(item.getName());
             nameView.setTextColor(Color.BLACK);
             dateView.setText(item.getDateString());

             if(this.position == position) {
                 info.setText(item.getTimeString() + "\n" + item.getDescription() + "\n" + item.getCampus_loc());
                 info.setGravity(Gravity.CENTER_VERTICAL);
                 info.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
             }else{
                 info.setText("");
             }

             return convertView;
         }

         public void selectedItem(int position) {
             this.position = position;
         }


         /*public void addItem(Event newEvent){
             events.add(newEvent);
             notifyDataSetChanged();
         }*/


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
