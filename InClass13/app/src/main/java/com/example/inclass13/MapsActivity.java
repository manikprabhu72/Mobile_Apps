package com.example.inclass13;

/*
    InClass 13
    Group 05
    Manik Prabhu Cheekoti
    Akhil Reddy Yakkaluri
    
 */

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        json = getJsonFromAssets(this, "trip.json");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(json!=null){
            List<Location> locations = getLocationsFromJson(json);
            PolylineOptions polylineOptions = new PolylineOptions();
            LatLngBounds.Builder latlngBuilder = new LatLngBounds.Builder();
            for(Location location : locations){
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                polylineOptions.add(latLng);
                latlngBuilder.include(latLng);
            }
            final LatLngBounds latLngBounds = latlngBuilder.build();
            Location startLoc = locations.get(0);
            Location endLoc = locations.get(locations.size()-1);
            LatLng startPos = new LatLng(startLoc.getLatitude(),startLoc.getLongitude());
            LatLng endPos = new LatLng(endLoc.getLatitude(),endLoc.getLongitude());
            mMap.addPolyline(polylineOptions);
            mMap.addMarker(new MarkerOptions().position(startPos).title("Start Location"));
            mMap.addMarker(new MarkerOptions().position(endPos).title("End Location"));
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50));
                }
            });

        }


    }

    public String getJsonFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return jsonString;
    }

    public List<Location> getLocationsFromJson(String json){
        Gson gson = new Gson();
        Locations locations = gson.fromJson(json, Locations.class);
        return locations.getPoints();
    }
}
