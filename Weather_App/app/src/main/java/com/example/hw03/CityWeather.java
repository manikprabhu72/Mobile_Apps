package com.example.hw03;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

public class CityWeather extends AppCompatActivity {

    ConstraintLayout cl_content, cl_loading;
    TextView tv_city,tv_headline,tv_min_max,tv_forecast_date,tv_day_weather,tv_night_weather, tv_more_details;
    ImageView iv_day,iv_night;
    Button bt_save_city, bt_set_current;
    RecyclerView rv_images;
    final String forecat_api_url = "http://dataservice.accuweather.com/forecasts/v1/daily/5day/";
    String currentMobileLink = "null";
    ArrayList<Forecast> forecastArrayList = new ArrayList<Forecast>();
    private final OkHttpClient client = new OkHttpClient();
    WeatherAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_weather);
        setTitle("City Weather");
        cl_content = findViewById(R.id.cl_content);
        cl_loading = findViewById(R.id.cl_loading_main);
        cl_content.setVisibility(ConstraintLayout.INVISIBLE);

        tv_city = findViewById(R.id.tv_city);
        tv_headline = findViewById(R.id.tv_headline);
        tv_min_max = findViewById(R.id.tv_min_max);
        tv_forecast_date = findViewById(R.id.tv_forecast_date);
        tv_day_weather = findViewById(R.id.tv_day_weather);
        tv_night_weather = findViewById(R.id.tv_night_weather);
        tv_more_details = findViewById(R.id.tv_more_details);

        iv_day = findViewById(R.id.iv_day);
        iv_night = findViewById(R.id.iv_night);

        bt_save_city = findViewById(R.id.bt_save_city);
        bt_set_current = findViewById(R.id.bt_set_current);
        rv_images = findViewById(R.id.rv_images);

        bt_save_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(MainActivity.SAVE_CODE,intent);
                finish();
            }
        });

        bt_set_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(MainActivity.SET_CODE,intent);
                finish();
            }
        });

        tv_more_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent implicit = new Intent(Intent.ACTION_VIEW, Uri.parse(currentMobileLink));
                startActivity(implicit);
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_images.setLayoutManager(manager);
        rv_images.setHasFixedSize(true);

        if(getIntent() !=null && getIntent().getExtras() != null){
            String api_key = getString(R.string.api_key);
            City city = (City) getIntent().getSerializableExtra(MainActivity.CITY_KEY);
            Log.d("City Weather: ", city.toString());
            tv_city.setText(city.getCityName()+", "+city.getCountryName());
            Request request = new Request.Builder()
                    .url(forecat_api_url+city.getCityKey()+"?apikey="+api_key)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        final String jsonData = responseBody.string();
                        Log.d("City Weather: ", jsonData);
                        if (!response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CityWeather.this, "Error: " + jsonData, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            final JSONObject jsonObject = new JSONObject(jsonData);
                            JSONArray forecastArr = jsonObject.getJSONArray("DailyForecasts");
                            for(int i=0; i<forecastArr.length();i++){
                                Forecast forecast = getForecastFromJSONObject(forecastArr.getJSONObject(i));
                                forecastArrayList.add(forecast);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        tv_headline.setText(jsonObject.getJSONObject("Headline").getString("Text"));
                                        setUI(forecastArrayList.get(0));
                                        myAdapter = new WeatherAdapter(CityWeather.this,forecastArrayList);
                                        rv_images.setAdapter(myAdapter);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e){
                        e.printStackTrace();
                    }

                }
            });

        }
    }

    public Forecast getForecastFromJSONObject(JSONObject jsonObject) throws JSONException,ParseException{
        Forecast forecast = new Forecast();
        Date dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(jsonObject.getString("Date"));
        forecast.setForecastDate(dateFormat);
        JSONObject dayObject = jsonObject.getJSONObject("Day");
        JSONObject nightObject = jsonObject.getJSONObject("Night");
        forecast.setDayIcon(dayObject.getInt("Icon"));
        forecast.setDayText(dayObject.getString("IconPhrase"));
        forecast.setNightIcon(nightObject.getInt("Icon"));
        forecast.setNightText(nightObject.getString("IconPhrase"));
        forecast.setMinTemp(jsonObject.getJSONObject("Temperature").getJSONObject("Minimum").getDouble("Value"));
        forecast.setMaxTemp(jsonObject.getJSONObject("Temperature").getJSONObject("Maximum").getDouble("Value"));
        forecast.setMobileLink(jsonObject.getString("MobileLink"));
        return forecast;
    };

    public void setUI(Forecast forecast) throws JSONException,ParseException{
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
        tv_forecast_date.setText("Forecast on "+formatter.format(forecast.getForecastDate()));
        tv_min_max.setText("Temparature "+forecast.getMaxTemp()+"/"+forecast.getMinTemp()+" F");
        tv_day_weather.setText(forecast.getDayText());
        tv_night_weather.setText(forecast.getNightText());
        currentMobileLink = forecast.getMobileLink();
        int dayIcon = forecast.getDayIcon();
        String dayIconStr = String.valueOf(dayIcon);
        if(dayIcon < 10){
            dayIconStr = "0"+dayIconStr;
        }
        int nightIcon = forecast.getNightIcon();
        String nightIconStr = String.valueOf(nightIcon);
        if(nightIcon < 10){
            nightIconStr = "0"+nightIconStr;
        }
        Picasso.get().load(MainActivity.image_url+dayIconStr+"-s.png").into(iv_day);
        Picasso.get().load(MainActivity.image_url+nightIconStr+"-s.png").into(iv_night);
        cl_loading.setVisibility(ConstraintLayout.INVISIBLE);
        cl_content.setVisibility(ConstraintLayout.VISIBLE);
    }
}
