package com.harshilhshah.rufree.rufree.Model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by harshilhshah on 9/28/15.
 */
public class Location {

    private static final long serialVersionUID = 1L;

    private String room;
    private String building;
    private String campus;
    private String address;
    private String city;
    private String state = "NJ";
    private final static String country = "US";

    public Location(){}

    public Location(String address, String city, String state){
        this.address = address;
        this.city = city;
        if(state != null)
            this.state = state;
    }

    public Location(String room, String building, String campus, String address, String city){
        this.room = room;
        this.building = building;
        this.campus = campus;
        this.address = address;
        this.city = city;
    }

    public Location(JSONObject loc) throws JSONException {
        this.room = loc.getString("event:room");
        this.building = loc.getString("event:location");
        this.campus = loc.getString("event:campus");
        this.address = loc.getString("event:address");
        this.city = loc.getString("event:city");
        this.state = loc.getString("event:state");
        if(this.state == null) this.state = "NJ";
    }

    public boolean createAddress(){
        if(this.address != null && this.city != null){
            return true;
        }else{
            return false;
        }
    }

    public boolean createCampus(){
        if(this.city != null && (this.address != null || this.building != null)){
            return true;
        }else{
            return false;
        }
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        if(state != null) this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public String toString(){
        return this.address + " " + this.city + " " + this.state;
    }



}
