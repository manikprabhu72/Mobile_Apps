package com.example.hw03;

/*
HW03
Group 05
Manik Prabhu Cheekoti
Akhil Reddy Yakkaluru
 */

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    Button bt_set_city,bt_search;
    ConstraintLayout cl_recycler_view,cl_set_city, cl_city_info, cl_no_save, cl_loading_main;
    AlertDialog searchDialog;
    AlertDialog changeCityDialog;
    RecyclerView rv_saved;
    final String location_api_base_url = "http://dataservice.accuweather.com/locations/v1/cities/";
    final String current_cond_base_url = "http://dataservice.accuweather.com/currentconditions/v1/";
    static final String image_url = "http://developer.accuweather.com/sites/default/files/";
    static final String CITY_KEY = "City";
    static final int REQUEST_CODE=0;
    static final int SAVE_CODE=1;
    static final int SET_CODE=2;
    static final String START_NEW = "Start";
    static final String UPDATE_UI = "Update";
    static final String SAVE="Save";
    String api_key = null;
    private final OkHttpClient client = new OkHttpClient();
    City currentCity = null;
    City startCity = null;
    ArrayList<City> searchCities = new ArrayList<City>();
    ArrayList<City> savedCities = new ArrayList<City>();
    String displayNames[];
    TextView tv_city_name,tv_weather_text, tv_temp, tv_time;
    ImageView iv_weather_icon;
    EditText et_city_main, et_country_main;

        SavedAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Weather App");
        api_key = getString(R.string.api_key);
        bt_set_city = findViewById(R.id.bt_set_city);
        bt_search = findViewById(R.id.bt_search);
        cl_recycler_view = findViewById(R.id.cl_recycler_view);
        cl_set_city = findViewById(R.id.cl_set_city);
        cl_city_info = findViewById(R.id.cl_city_info);
        cl_no_save = findViewById(R.id.cl_no_save);
        cl_loading_main = findViewById(R.id.cl_loading_main);

        cl_loading_main.setVisibility(ConstraintLayout.INVISIBLE);
        cl_recycler_view.setVisibility(ConstraintLayout.INVISIBLE);
        cl_city_info.setVisibility(ConstraintLayout.INVISIBLE);


        et_city_main = findViewById(R.id.et_city_main);
        et_country_main = findViewById(R.id.et_country_main);

        tv_city_name = findViewById(R.id.tv_city_name);
        tv_weather_text = findViewById(R.id.tv_weather_text);
        tv_time = findViewById(R.id.tv_time);
        tv_temp = findViewById(R.id.tv_temp);
        iv_weather_icon = findViewById(R.id.iv_weather_icon);

        rv_saved = findViewById(R.id.rv_saved);

        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        rv_saved.setLayoutManager(manager);
        rv_saved.setHasFixedSize(true);
        myAdapter = new SavedAdapter(MainActivity.this,savedCities);
        rv_saved.setAdapter(myAdapter);

        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationApi(et_country_main.getText().toString(),et_city_main.getText().toString(),true);
            }
        });

        final View customLayout = getLayoutInflater().inflate(R.layout.alert_layout,null);
        Button bt_ok = customLayout.findViewById(R.id.bt_ok);
        Button bt_cancel = customLayout.findViewById(R.id.bt_cancel);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter City Details");
        builder.setView(customLayout);
        final AlertDialog alert = builder.create();
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_city = customLayout.findViewById(R.id.et_city);
                EditText et_country = customLayout.findViewById(R.id.et_country);
                cl_loading_main.setVisibility(ConstraintLayout.VISIBLE);
                locationApi(et_country.getText().toString(),et_city.getText().toString(),false);
                alert.dismiss();
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        tv_city_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentCity != null){
                    EditText et_city = customLayout.findViewById(R.id.et_city);
                    EditText et_country = customLayout.findViewById(R.id.et_country);
                    et_city.setText(currentCity.getCityName());
                    et_country.setText(currentCity.getCountryName());
                }
                alert.show();
            }
        });

        bt_set_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.show();
            }
        });
        if(isConnected()){
            if(currentCity != null){
                Log.d("Main", "Current City Updated");
                currentCondApi(currentCity.getCityKey(),UPDATE_UI);
            }

        }else{
            Toast.makeText(this,"Check Internet Connection!!!", Toast.LENGTH_SHORT).show();
        }



    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    public void locationApi(String country, String city, final boolean startNew) {
        Request request = new Request.Builder()
                .url(location_api_base_url+country+"/search?apikey="+api_key+"&q="+city)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    final String jsonData = responseBody.string();
                    Log.d("Main Location Data", jsonData);
                    if (!response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Error: " + jsonData, Toast.LENGTH_SHORT).show();
                                cl_loading_main.setVisibility(ConstraintLayout.INVISIBLE);
                            }
                        });
                    } else {
                        JSONArray jsonArray = new JSONArray(jsonData);
                        int arrLen = jsonArray.length();
                        if(arrLen > 0){
                            JSONObject cityObject;
                            City city;
                            if(startNew){
                                displayNames = new String[arrLen];
                                for(int i=0; i < arrLen;i++){
                                    cityObject = jsonArray.getJSONObject(i);
                                    city = getCityFromJSONObject(cityObject);
                                    displayNames[i] = city.getCityName() +", "+city.getAdminArea();
                                    searchCities.add(city);
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                        builder.setTitle("Select City");
                                        builder.setItems(displayNames, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startCity = searchCities.get(which);
                                                currentCondApi(startCity.getCityKey(), START_NEW);
                                                searchCities = new ArrayList<City>();
                                            }
                                        });
                                        changeCityDialog = builder.create();
                                        changeCityDialog.show();
                                    }
                                });

                            }else{
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                currentCity = getCityFromJSONObject(jsonObject);
                                currentCondApi(currentCity.getCityKey(), UPDATE_UI);
                            }

                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "City Not Found", Toast.LENGTH_LONG).show();
                                    cl_loading_main.setVisibility(ConstraintLayout.INVISIBLE);
                                }
                            });

                        }

                    }

                    Log.d("Main Location Api: ", jsonData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public City getCityFromJSONObject(JSONObject cityObject) throws JSONException{
        City city = new City();
        city.setCityKey(cityObject.getString("Key"));
        city.setCityName(cityObject.getString("EnglishName"));
        city.setCountryName(cityObject.getJSONObject("Country").getString("ID"));
        city.setAdminArea(cityObject.getJSONObject("AdministrativeArea").getString("ID"));
        return city;
    };

    public void addPropToCity(City city, JSONObject weatherObject) throws JSONException,ParseException{
        Date dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(weatherObject.getString("LocalObservationDateTime"));
        city.setLocalObservationDateTime(dateFormat);
        city.setWeatherText(weatherObject.getString("WeatherText"));
        city.setWeatherIcon(weatherObject.getInt("WeatherIcon"));
        city.setMetricTemp(weatherObject.getJSONObject("Temperature").getJSONObject("Metric").getDouble("Value"));
    };

    public void setCurrentCityUI(){
        cl_set_city.setVisibility(ConstraintLayout.INVISIBLE);
        cl_city_info.setVisibility(ConstraintLayout.VISIBLE);
        tv_city_name.setText(currentCity.getCityName()+", "+currentCity.getCountryName());
        tv_weather_text.setText(currentCity.getWeatherText());
        tv_temp.setText("Temperature: "+currentCity.getMetricTemp()+" C");
        Date currentDate = new Date();
        long minutes = (currentDate.getTime() - currentCity.getLocalObservationDateTime().getTime())/(60 * 1000);
        tv_time.setText("Updated: "+minutes+" minutes ago");
        int icon = currentCity.getWeatherIcon();
        String iconStr = String.valueOf(icon);
        if(icon < 10){
            iconStr = "0"+iconStr;
        }
        Picasso.get().load(image_url+iconStr+"-s.png").into(iv_weather_icon);
        Toast.makeText(MainActivity.this, "Current city details saved", Toast.LENGTH_LONG).show();
    }

    public void currentCondApi(String cityKey, final String action){

        Request request = new Request.Builder()
                .url(current_cond_base_url+cityKey+"?apikey="+api_key)
                .build();
        client.newCall(request).enqueue(new Callback(){

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    final String jsonData = responseBody.string();
                    Log.d("Main Current Condition", jsonData);
                    if (!response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Error: " + jsonData, Toast.LENGTH_SHORT).show();
                                cl_loading_main.setVisibility(ConstraintLayout.INVISIBLE);
                            }
                        });
                    } else {
                        JSONArray jsonArray = new JSONArray(jsonData);
                        JSONObject weatherObject = jsonArray.getJSONObject(0);
                        if(UPDATE_UI.equals(action)){
                            addPropToCity(currentCity,weatherObject);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setCurrentCityUI();
                                    cl_loading_main.setVisibility(ConstraintLayout.INVISIBLE);
                                }
                            });
                        }else if(START_NEW.equals(action)){
                            addPropToCity(startCity, weatherObject);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(MainActivity.this, CityWeather.class);
                                    intent.putExtra(MainActivity.CITY_KEY,startCity);
                                    Log.d("Start City Weather: ", "Yes");
                                    startActivityForResult(intent,MainActivity.REQUEST_CODE);
                                }
                            });

                        }else{
                            addPropToCity(startCity, weatherObject);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if(resultCode == SET_CODE){
                Log.d("ughu ", startCity.getCityKey()+" "+startCity.getCityName());
                Log.d("guhgu ", currentCity.getCityKey()+" "+startCity.getCityName());
                if(startCity.getCityKey().equals(currentCity.getCityKey())){
                    Log.d("vvj: ", "equals");
                    Toast.makeText(MainActivity.this,"Current City Updated",Toast.LENGTH_SHORT).show();
                }else{
                    Log.d("vvj: ", "not equals");
                    currentCity = startCity;
                    Toast.makeText(MainActivity.this,"Current City Saved",Toast.LENGTH_SHORT).show();
                }
                currentCondApi(currentCity.getCityKey(),UPDATE_UI);
            }
            if(resultCode == SAVE_CODE){
                if(!savedCities.contains(startCity)){
                    savedCities.add(startCity);
                    Toast.makeText(MainActivity.this,"City Saved",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"City Updated",Toast.LENGTH_SHORT).show();
                }
                currentCondApi(startCity.getCityKey(),SAVE);
                if(currentCity != null){
                    currentCondApi(currentCity.getCityKey(), UPDATE_UI);
                }
                myAdapter.notifyDataSetChanged();
                cl_no_save.setVisibility(ConstraintLayout.INVISIBLE);
                cl_recycler_view.setVisibility(ConstraintLayout.VISIBLE);
            }
        }
    }
}
