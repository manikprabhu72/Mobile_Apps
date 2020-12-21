package com.example.inclass14;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Trip implements Serializable {
    private String name;
    private String docId;
    private String place_id;
    private String description;
    private Double lat, lng;
    private ArrayList<Place> results;
    HashMap<String, Object> hashMap;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Place> getResults() {
        return results;
    }

    public void setResults(ArrayList<Place> results) {
        this.results = results;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public HashMap<String, Object> toHashMap(){
        hashMap =  new HashMap<String, Object>();
        hashMap.put("name",getName());
        hashMap.put("place_id",getPlace_id());
        hashMap.put("description", getDescription());
        hashMap.put("lat", getLat());
        hashMap.put("lng", getLng());
        hashMap.put("places",getResults());
        return hashMap;
    }

    public static Trip toTrip(DocumentSnapshot documentSnapshot){
        Trip trip = new Trip();
        trip.setDocId(documentSnapshot.getId());
        trip.setName(documentSnapshot.getString("name"));
        trip.setPlace_id(documentSnapshot.getString("place_id"));
        trip.setDescription(documentSnapshot.getString("description"));
        trip.setLat(documentSnapshot.getDouble("lat"));
        trip.setLng(documentSnapshot.getDouble("lng"));
        ArrayList<HashMap<String,Object>> docs= (ArrayList<HashMap<String,Object>>) documentSnapshot.get("places");
        ArrayList<Place> places = new ArrayList<Place>();
        if(docs != null && docs.size() !=0 ){
            for(int i=0; i < docs.size() ; i++){
                places.add(Place.toPlace(docs.get(i)));
            }
        }
       trip.setResults(places);
        return trip;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "name='" + name + '\'' +
                ", docId='" + docId + '\'' +
                ", place_id='" + place_id + '\'' +
                ", description='" + description + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", results=" + results +
                '}';
    }
}
