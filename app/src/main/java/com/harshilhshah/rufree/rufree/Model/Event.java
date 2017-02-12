package com.harshilhshah.rufree.rufree.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Created by harshilhshah on 9/25/15.
 */
public class Event implements Parcelable{

    private static final long serialVersionUID = 1L;
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public String name;
    public String id;

    private String startTime;
    private String endTime;
    private GregorianCalendar startGC = (GregorianCalendar) GregorianCalendar.getInstance();
    private GregorianCalendar endGC = (GregorianCalendar) GregorianCalendar.getInstance();;

    private String tags;
    private String description;
    private String image_url;
    private Location campus_loc;
    private String category;
    private String organization;
    private InputStream image;


    public Event( String name, String description, GregorianCalendar start, GregorianCalendar end, Location loc)
    {
        this.name = name;
        this.startGC = start;
        this.endGC = end;
        this.description = description;
        this.campus_loc = loc;
    }

    protected Event(Parcel in) {
        name = in.readString();
        id = in.readString();
        tags = in.readString();
        description = in.readString();
        image_url = in.readString();
        category = in.readString();
        organization = in.readString();
        String[] temp_location = in.readString().split(":");
        campus_loc = new Location(temp_location[0],temp_location[1],temp_location[2]);
        startTime = in.readString();
        endTime = in.readString();
        try {
            startGC.setTime(formatter.parse(startTime));
            endGC.setTime(formatter.parse(endTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public InputStream getImage() {
        return image;
    }

    public void setImage(InputStream image) {
        this.image = image;
    }

    public Event(String name, String tags, String description, String start, String end, Location loc, String url) throws ParseException {
        this.name = name;
        this.description = description;
        this.campus_loc = loc;
        this.startTime = start;
        this.endTime = end;
        this.tags = tags;
        startGC.setTime(formatter.parse(start));
        endGC.setTime(formatter.parse(end));
        setImage_url(url);
    }

    public String getName() {
        return name;
    }

    public String getImage_url() {
        return image_url;
    }

    private void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Location getCampus_loc() {
        return campus_loc;
    }

    public void setCampus_loc(Location campus_loc) {
        this.campus_loc = campus_loc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GregorianCalendar getStartGC() {
        return startGC;
    }

    public void setStartGC(GregorianCalendar startGC) {
        this.startGC = startGC;
    }

    public GregorianCalendar getEndGC() {
        return endGC;
    }

    public void setEndGC(GregorianCalendar endGC) {
        this.endGC = endGC;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDateString(){
        SimpleDateFormat monthf = new SimpleDateFormat("MMM d");
        return monthf.format(startGC.getTime()).toUpperCase() ;
    }

    public String getTimeString(){
        SimpleDateFormat timef = new SimpleDateFormat("MMM d");
        if(timef.format(startGC.getTime()).equals(timef.format(endGC.getTime()))){
            timef = new SimpleDateFormat("EEEE, MMM d '\n'h:mm a '-'");
            String st = timef.format(startGC.getTime());
            timef = new SimpleDateFormat("h:mm a");
            return st + " " + timef.format(endGC.getTime());
        }else{
            return timef.format(startGC.getTime()) + "-" + timef.format(endGC.getTime());
        }
    }


    @Override
    public String toString() {
        String resultString = "";

        resultString += "Name: " + getName() + "\n";
        resultString +="Date: " + getDateString() + "\n";
        resultString +="\n\n\n";
        return resultString;
    }

    @Override
    public boolean equals(Object o){
        if (o == this) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        Event e = (Event)o;
        return e.getName().equals(this.getName()) && e.getDateString().equals(this.getDateString());
    }

    public String getSmsString(){
        return "Hey! #RUFreeFood @ " + this.name + " on "
                + this.getDateString() + ": " + this.description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(id);
        parcel.writeString(tags);
        parcel.writeString(description);
        parcel.writeString(image_url);
        parcel.writeString(category);
        parcel.writeString(organization);
        parcel.writeString(campus_loc.toString());
        parcel.writeString(startTime);
        parcel.writeString(endTime);
    }
}
