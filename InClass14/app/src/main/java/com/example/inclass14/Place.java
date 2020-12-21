package com.example.inclass14;

import java.io.Serializable;
import java.util.HashMap;

public class Place implements Serializable {
    private String place_id;
    private String name;
    private String icon;
    private double lat, lng;
    HashMap<String, Object> hashMap;

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public HashMap<String, Object> toHashMap(){
        hashMap =  new HashMap<String, Object>();
        hashMap.put("place_id",getPlace_id());
        hashMap.put("name", getName());
        hashMap.put("lat", getLat());
        hashMap.put("lng", getLng());
        hashMap.put("icon",getIcon());
        return hashMap;
    }

    public static Place toPlace(HashMap<String,Object> placeMap){
        Place place = new Place();
        place.setIcon(String.valueOf(placeMap.get("icon")));
        place.setPlace_id(String.valueOf(placeMap.get("place_id")));
        place.setName(String.valueOf(placeMap.get("name")));
        place.setLat((Double) placeMap.get("lat"));
        place.setLng((Double) placeMap.get("lng"));
        return place;
    }

    @Override
    public String toString() {
        return "Place{" +
                "place_id='" + place_id + '\'' +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
