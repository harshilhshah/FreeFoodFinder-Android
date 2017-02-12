package com.harshilhshah.rufree.rufree.Utility;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.harshilhshah.rufree.rufree.EventAdapter;
import com.harshilhshah.rufree.rufree.Model.Event;
import com.harshilhshah.rufree.rufree.Model.Location;
import com.harshilhshah.rufree.rufree.R;

import java.text.ParseException;
import java.util.HashMap;

/**
 * Created by harshilhshah on 9/27/15.
 */
public class EventScraper {

    private static final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://crackling-heat-4631.firebaseio.com/source");
    private static final String[] food_tags = {
            "appetizer","snack","pizza","lunch","dinner","breakfast","meal","candy",
            "drinks","punch"," serving","pie ","cake","soda","chicken","wings","burger",
            "burrito","bagel","popcorn","cream","donut","beer","food","dessert","chocolate",
            "subs ","hoagie","sandwich","turkey","supper","brunch","takeout","refreshment",
            "beverage","cookie","brownie","chips","soup","grill","bbq","barbecue","tacos"
    };
    private EventAdapter eventAdapter;

    public EventScraper(EventAdapter adapter){
        eventAdapter = adapter;
        getEvents();
    }

    public static int getRandomImage(Event e){
        switch (e.getTags().split("#")[1].trim()){
            case "food":
                return R.drawable.pasta;
            case "dessert":
                return R.drawable.dessert;
            case "snack":
                return R.drawable.cookies;
            default:
                Log.d("klkl",e.getTags().split("#")[1]);
                return R.drawable.lunch;
        }
    }



    private void getEvents() {

        dbRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {

                for (DataSnapshot sourceSnapshot: snapshot.getChildren()) {

                    for(DataSnapshot eventSnapshot: sourceSnapshot.getChildren()){
                            Log.d("Tag",eventSnapshot.getValue().toString());
                            HashMap event = (HashMap) eventSnapshot.getValue();

                            String description = "No description";
                            if(event.get("description") != null) {
                                description = event.get("description").toString();
                            }

                            Location loc = null;
                            String tags = getTags(description);

                            if (tags.length() > 1) {

                                if (event.get("place") != null) {
                                    HashMap place = (HashMap) event.get("place");
                                    if (place.get("location") != null) {
                                        HashMap locMap = (HashMap) place.get("location");
                                        if(locMap.get("street") != null && locMap.get("city") != null && locMap.get("state") != null) {
                                            loc = new Location(locMap.get("street").toString(),
                                                    locMap.get("city").toString(), locMap.get("state").toString());
                                        }
                                        else{
                                            loc = new Location(place.get("name").toString());
                                        }
                                    } else {
                                        loc = new Location(place.get("name").toString());
                                    }
                                }

                                String title = event.get("name").toString();
                                String img_url = (event.get("cover") != null) ? ((HashMap) event.get("cover")).get("source").toString() : "";
                                String start_time = event.get("start_time").toString();
                                String end_time = start_time;
                                if (event.get("end_time") != null)
                                    end_time = event.get("end_time").toString();

                                try {
                                    eventAdapter.addItem(new Event(title, tags, description, start_time, end_time, loc, img_url));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }

                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }

        });

    }



    /** This function takes the event description string and looks for
     *  the specified parameters.
     */
    private static String getTags(String descr){
        String ret = "";
        for(String word: food_tags)
            if(descr != null && descr.toLowerCase().contains(word))
               ret += "#" + word + " ";
        return ret;
    }

}
