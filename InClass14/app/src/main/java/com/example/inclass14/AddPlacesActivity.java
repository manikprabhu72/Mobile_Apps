package com.example.inclass14;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

public class AddPlacesActivity extends AppCompatActivity implements PlaceMainAdapter.PlaceMainAdapterActivity{

    RecyclerView rv_places;
    PlaceMainAdapter placeMainAdapter;
    ArrayList<Place> placesList;
    private final OkHttpClient client = new OkHttpClient();
    int tripPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_places);
        setTitle("Add Places");
        rv_places = findViewById(R.id.rv_places);

        placesList = new ArrayList<Place>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv_places.setLayoutManager(layoutManager);
        rv_places.setHasFixedSize(true);

        if(getIntent() != null && getIntent().getExtras()!=null){
            tripPosition = getIntent().getExtras().getInt("tripPosition");
            Double lat = getIntent().getExtras().getDouble("lat");
            Double lng = getIntent().getExtras().getDouble("lng");
            Request request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?key="+getString(R.string.api_key)+"&radius=1000&location="+lat+","+lng)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddPlacesActivity.this,"Error in API",Toast.LENGTH_SHORT).show();
                        }
                    });

                    e.printStackTrace();
                }

                @Override public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        final String jsonData = responseBody.string();

                        JSONObject jsonObject = new JSONObject(jsonData);
                        if (!response.isSuccessful()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AddPlacesActivity.this,"Error: "+jsonData, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            for(int i=0; i < jsonArray.length();i++){
                                JSONObject placeJSON = jsonArray.getJSONObject(i);
                                Place place = new Place();
                                JSONObject placeGeoObj = placeJSON.getJSONObject("geometry").getJSONObject("location");
                                Log.d("Search Places: ",placeGeoObj.toString());
                                place.setLat(placeGeoObj.getDouble("lat"));
                                place.setLng(placeGeoObj.getDouble("lng"));
                                place.setPlace_id(placeJSON.getString("place_id"));
                                place.setName(placeJSON.getString("name"));
                                place.setIcon(placeJSON.getString("icon"));
                                placesList.add(place);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    placeMainAdapter = new PlaceMainAdapter(AddPlacesActivity.this, placesList);
                                    rv_places.setAdapter(placeMainAdapter);
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

    @Override
    public void addPlace(int position) {
        Intent i = new Intent();
        i.putExtra("place", placesList.get(position));
        i.putExtra("tripPosition",tripPosition);
        setResult(RESULT_OK,i);
        finish();
    }
}
