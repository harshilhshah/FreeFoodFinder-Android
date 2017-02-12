package com.harshilhshah.rufree.rufree.Model;

/**
 * Created by harshilhshah on 9/28/15.
 */
public class Location {

    private static final long serialVersionUID = 1L;

    private String address;
    private String city;
    private String state = "NJ";
    private final static String country = "US";


    public Location(String address, String city, String state){
        this.address = address;
        this.city = city;
        if(state != null)
            this.state = state;
    }

    public Location(String address) {
        this.address = address;
    }

    public boolean createAddress(){
        if(this.address != null && this.city != null){
            return true;
        }else{
            return false;
        }
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
        return this.address + ":" + this.city + ":" + this.state;
    }



}
