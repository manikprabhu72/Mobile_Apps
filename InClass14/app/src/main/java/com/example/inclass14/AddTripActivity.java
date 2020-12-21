package com.example.inclass14;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AddTripActivity extends AppCompatActivity implements CityAdapter.CityAdapterActivity{

    Button bt_search, bt_add_trip;
    EditText et_trip_name, et_city;
    RecyclerView rv_places;
    String trip_name, city;
    FirebaseFirestore db;
    ArrayList<Trip> placesList;
    CityAdapter cityAdapter;
    Trip selectedTrip;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        setTitle("Add Trip");
        bt_search = findViewById(R.id.bt_search);
        bt_add_trip = findViewById(R.id.bt_add_trip);

        et_trip_name = findViewById(R.id.et_trip_name);
        et_city = findViewById(R.id.et_city);
        rv_places = findViewById(R.id.rv_places);

        db = FirebaseFirestore.getInstance();

        placesList = new ArrayList<Trip>();


        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv_places.setLayoutManager(layoutManager);
        rv_places.setHasFixedSize(true);

        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trip_name = et_trip_name.getText().toString();
                city = et_city.getText().toString();

                if(trip_name.isEmpty() || city.isEmpty() ){
                    Toast.makeText(AddTripActivity.this,"Please enter city name and trip name",Toast.LENGTH_SHORT).show();
                }else{
                    Request request = new Request.Builder()
                            .url("https://maps.googleapis.com/maps/api/place/autocomplete/json?key="+getString(R.string.api_key)+"&types=(cities)&input="+city)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AddTripActivity.this,"Error in API",Toast.LENGTH_SHORT).show();
                                }
                            });
                            e.printStackTrace();
                        }

                        @Override public void onResponse(Call call, Response response) throws IOException {
                            try (ResponseBody responseBody = response.body()) {
                                final String jsonData = responseBody.string();
                                Log.d("Search City: ",jsonData);
                                JSONObject jsonObject = new JSONObject(jsonData);
                                if (!response.isSuccessful()){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(AddTripActivity.this,"Error: "+jsonData, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    JSONArray jsonArray = jsonObject.getJSONArray("predictions");
                                    for(int i=0; i < jsonArray.length();i++){
                                        JSONObject placeJSON = jsonArray.getJSONObject(i);
                                        Trip trip = new Trip();
                                        trip.setPlace_id(placeJSON.getString("place_id"));
                                        trip.setDescription(placeJSON.getString("description"));
                                        placesList.add(trip);
                                    }

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            cityAdapter = new CityAdapter(AddTripActivity.this, placesList);
                                            rv_places.setAdapter(cityAdapter);
                                        }
                                    });
                                }

                                Log.d("Login",jsonData);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

            }
        });

        bt_add_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedTrip!=null){
                    Request request = new Request.Builder()
                            .url("https://maps.googleapis.com/maps/api/place/details/json?key="+getString(R.string.api_key)+"&placeid="+selectedTrip.getPlace_id())
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override public void onFailure(Call call, IOException e) {
                            Toast.makeText(AddTripActivity.this,"Error in API",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        @Override public void onResponse(Call call, Response response) throws IOException {
                            try (ResponseBody responseBody = response.body()) {
                                final String jsonData = responseBody.string();
                                Log.d("Get Geo Cord: ",jsonData);
                                JSONObject jsonObject = new JSONObject(jsonData);
                                if (!response.isSuccessful()){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(AddTripActivity.this,"Error: "+jsonData, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    JSONObject geoJsonObject = jsonObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
                                    selectedTrip.setLat(geoJsonObject.getDouble("lat"));
                                    selectedTrip.setLng(geoJsonObject.getDouble("lng"));
                                    selectedTrip.setName(et_trip_name.getText().toString());
                                    db.collection(MainActivity.COLLECTION).add(selectedTrip.toHashMap()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d("AddTrip", "DocumentSnapshot written with ID: " + documentReference.getId());
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("AddTrip", "Error adding document", e);
                                            Toast.makeText(AddTripActivity.this,"Error adding to firestore", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    Toast.makeText(AddTripActivity.this,"Please search for city first and select a city from the list",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(int position) {
        selectedTrip = placesList.get(position);
        et_city.setText(selectedTrip.getDescription());
    }

}
