package com.harshilhshah.rufree.rufree.Model;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Created by harshilhshah on 9/25/15.
 */
public class Event {

    private static final long serialVersionUID = 1L;

    public String name;

    private GregorianCalendar startDate;
    private GregorianCalendar endDate;

    private String description;
    private String image_url;
    private Location campus_loc;
    private String category;
    private String organization;
    private InputStream image;


    public Event(String name, String description, GregorianCalendar start, GregorianCalendar end, Location loc)
    {
        this.name = name;
        this.startDate = start;
        this.endDate = end;
        this.description = description;
        this.campus_loc = loc;
    }

    public InputStream getImage() {
        return image;
    }

    public void setImage(InputStream image) {
        this.image = image;
    }

    public Event(String name, String description, GregorianCalendar start, GregorianCalendar end, Location loc, String url)
    {
        this.name = name;
        this.startDate = start;
        this.endDate = end;
        this.description = description;
        this.campus_loc = loc;
        setImage_url(url);
    }

    public String getName() {
        return name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
        try {
            InputStream is = (InputStream) new URL(this.image_url).getContent();
            setImage(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public GregorianCalendar getStartDate() {
        return startDate;
    }

    public void setStartDate(GregorianCalendar startDate) {
        this.startDate = startDate;
    }

    public GregorianCalendar getEndDate() {
        return endDate;
    }

    public void setEndDate(GregorianCalendar endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateString(){
        SimpleDateFormat monthf = new SimpleDateFormat("MMM d");
        return monthf.format(startDate.getTime()).toUpperCase() ;
    }

    public String getTimeString(){
        SimpleDateFormat timef = new SimpleDateFormat("MMM d");
        if(timef.format(startDate.getTime()).equals(timef.format(endDate.getTime()))){
            timef = new SimpleDateFormat("EEEE, MMM d 'from' h:mma");
            String st = timef.format(startDate.getTime());
            timef = new SimpleDateFormat("h:mma");
            return st + " to " + timef.format(endDate.getTime());
        }else{
            return timef.format(startDate.getTime()) + "-" + timef.format(endDate.getTime());
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

    public String getSmsString(){
        return "Hey! #RUFreeFood @ " + this.name + " on "
                + this.getDateString() + ": " + this.description;
    }

}
