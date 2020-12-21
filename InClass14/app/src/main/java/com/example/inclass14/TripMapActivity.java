package com.example.inclass14;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class TripMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_map);
        setTitle("Trip Map");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if(getIntent()!=null && getIntent().getExtras()!=null){
            trip = (Trip) getIntent().getSerializableExtra("Trip");
        }
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
        if(trip !=null){
            List<Place> places = trip.getResults();
            Log.d("MapsAct", places.toString());
            LatLngBounds.Builder latlngBuilder = new LatLngBounds.Builder();
            for(Place place : places){
                LatLng latLng = new LatLng(place.getLat(),place.getLng());
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                latlngBuilder.include(marker.getPosition());
            }
            LatLng latLngTrip = new LatLng(trip.getLat(),trip.getLng());
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLngTrip).title(trip.getDescription()));
            latlngBuilder.include(marker.getPosition());
            final LatLngBounds latLngBounds = latlngBuilder.build();


            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 200));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 200));
                }
            });
        }
    }
}
