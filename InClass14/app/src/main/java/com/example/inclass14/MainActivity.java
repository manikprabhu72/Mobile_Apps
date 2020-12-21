package com.example.inclass14;

/*
InClass 14
Group 05
Manik Prabhu Cheekoti
Akhil Reddy Yakkaluri
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TripAdapter.TripAdapterActivity, PlaceAdapter.PlaceAdapterActivity {
    ImageButton ib_add_trip;
    RecyclerView rv_trips;
    static final int REQ_CODE_ADD_TRIP = 0;
    static final int REQ_CODE_ADD_PLACE = 1;
    static final String COLLECTION = "trips";
    FirebaseFirestore db;
    ArrayList<Trip> tripList;
    TripAdapter tripAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Trips");

        ib_add_trip = findViewById(R.id.ib_add_trip);
        rv_trips = findViewById(R.id.rv_trips);

        db = FirebaseFirestore.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        rv_trips.setLayoutManager(layoutManager);
        rv_trips.setHasFixedSize(true);

        tripList = new ArrayList<Trip>();

        ib_add_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AddTripActivity.class);
                startActivityForResult(i,REQ_CODE_ADD_TRIP);
            }
        });
        readData(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  final Intent data) {
        Log.d("MainActivity", "Before");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_ADD_TRIP && resultCode == RESULT_OK) {
            Log.d("MainActivity", "After");
            readData(false);
        }else if (requestCode == REQ_CODE_ADD_PLACE && resultCode == RESULT_OK) {
            Log.d("MainActivity", "After");
            final Place place = (Place) data.getExtras().getSerializable("place");
            int tripPosition = data.getExtras().getInt("tripPosition");
            final Trip trip = tripList.get(tripPosition);
            final ArrayList<Place> places = trip.getResults();
            places.add(place);
            trip.setResults(places);
            db.collection(COLLECTION).document(trip.getDocId()).update(trip.toHashMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    tripAdapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Add Place to DB Failed",Toast.LENGTH_SHORT).show();
                        }
                    });
                    places.remove(place);
                    trip.setResults(places);
                }
            });
        }
    }

    @Override
    public void onClickAddPlace(int position) {
        Intent i = new Intent(MainActivity.this, AddPlacesActivity.class);
        i.putExtra("tripPosition", position);
        i.putExtra("lat", tripList.get(position).getLat());
        i.putExtra("lng", tripList.get(position).getLng());
        startActivityForResult(i,REQ_CODE_ADD_PLACE);
    }

    @Override
    public void onClickLocation(int position) {
        Intent i = new Intent(MainActivity.this,TripMapActivity.class);
        i.putExtra("Trip",tripList.get(position));
        startActivity(i);
    }

    @Override
    public void deletePlace(final int tripPosition, int position) {
        final ArrayList<Place> places = tripList.get(tripPosition).getResults();
        Log.d("delete: ", places.toString());
        Log.d("delete: ", tripPosition +" position of place" + position);
        Log.d("delete: ", tripList.toString());
        final Place place = places.remove(position);
        tripList.get(tripPosition).setResults(places);
        db.collection(COLLECTION).document(tripList.get(tripPosition).getDocId()).update(tripList.get(tripPosition).toHashMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                tripAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Delete Place to DB Failed",Toast.LENGTH_SHORT).show();
                    }
                });
                places.add(place);
                tripList.get(tripPosition).setResults(places);
            }
        });

    }

    public void readData(final boolean newAdap){
        db.collection(COLLECTION).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null && queryDocumentSnapshots.getDocuments() != null && queryDocumentSnapshots.getDocuments().size() !=0){
                    tripList.clear();
                    for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                        Trip trip = Trip.toTrip(documentSnapshot);
                        tripList.add(trip);
                    }
                    if(tripAdapter == null){
                        tripAdapter = new TripAdapter(MainActivity.this, tripList);
                        rv_trips.setAdapter(tripAdapter);
                    }else{
                        tripAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

}
