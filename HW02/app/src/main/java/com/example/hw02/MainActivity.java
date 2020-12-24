package com.example.hw02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/*
HW02
Group05
Manik Prabhu Cheekoti
Akhil Reddy Yakkaluri
 */

public class MainActivity extends AppCompatActivity {

    RecyclerView rv_results = null;
    MyAdapter myAdapter;
    EditText et_search = null;
    RecyclerView.LayoutManager mLayoutManager;
    ConstraintLayout cl_loading_main = null;
    ConstraintLayout cl_loading = null;
    ConstraintLayout cl_content = null;
    SeekBar sb_limit = null;
    Button bt_search = null;
    Button bt_reset = null;
    Switch st_sort;
    String baseURL = "https://itunes.apple.com/search";
    static final String TRACK_KEY = "track";
    ArrayList<Track> trackListMain = new ArrayList<Track>();

    TextView tv_limit=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("iTunes Music Search");

        rv_results = findViewById(R.id.rv_results);
        cl_loading_main = findViewById(R.id.cl_loading_main);
        cl_loading = findViewById(R.id.cl_loading);
        cl_content = findViewById(R.id.cl_content);
        bt_search = findViewById(R.id.bt_search);
        bt_reset = findViewById(R.id.bt_reset);
        sb_limit = findViewById(R.id.sb_limit);
        tv_limit = findViewById(R.id.tv_limit);
        et_search = findViewById(R.id.et_search);
        st_sort = findViewById(R.id.st_sort);
        rv_results.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL));
        cl_loading_main.setVisibility(ConstraintLayout.INVISIBLE);
        cl_loading_main.setBackgroundColor(Color.DKGRAY);
        cl_loading_main.getBackground().setAlpha(200);
        cl_loading.setBackgroundColor(Color.WHITE);

        /*Track test = new Track();
        test.title = "ghvjbk";
        test.album = " hgvjbk";
        test.trackPrice = 12.0;
        test.albumPrice = 12.0;
        trackListMain.add(test);*/
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        rv_results.setLayoutManager(manager);
        rv_results.setHasFixedSize(true);
        myAdapter = new MyAdapter(MainActivity.this,trackListMain);
        rv_results.setAdapter(myAdapter);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = et_search.getText().toString().trim().replace(" ","+");
                if(keyword.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please enter a search keyword", Toast.LENGTH_SHORT).show();
                }else{
                    new GetJSONData().execute(keyword,Integer.toString(sb_limit.getProgress()));
                }

            }
        });
        st_sort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Collections.sort(trackListMain, new Comparator<Track>() {
                        @Override
                        public int compare(Track o1, Track o2) {
                            try {
                                return new SimpleDateFormat("MM-dd-YYYY").parse(o1.releaseDate).compareTo(new SimpleDateFormat("MM-dd-YYYY").parse(o2.releaseDate));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return 0;
                        }
                    });
                }else{
                    Collections.sort(trackListMain, new Comparator<Track>() {
                        @Override
                        public int compare(Track o1, Track o2) {
                            return ((Double)(o1.trackPrice - o2.trackPrice)).intValue();
                        }
                    });
                }
                myAdapter.notifyDataSetChanged();
            }
        });
        bt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search.setText("");
                sb_limit.setProgress(10);
                trackListMain.clear();
                myAdapter.notifyDataSetChanged();
                st_sort.setChecked(true);
            }
        });



        if(isConnected()){

            sb_limit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    tv_limit.setText("Limit: "+progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        } else{
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

    class GetJSONData extends AsyncTask<String, Void, ArrayList<Track>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cl_loading_main.setVisibility(ConstraintLayout.VISIBLE);
            cl_loading_main.bringToFront();
            trackListMain.clear();
        }

        @Override
        protected void onPostExecute(ArrayList<Track> trackList) {
            super.onPostExecute(trackList);
            cl_loading_main.setVisibility(ConstraintLayout.INVISIBLE);
            if(st_sort.isChecked()){
                Collections.sort(trackList, new Comparator<Track>() {
                    @Override
                    public int compare(Track o1, Track o2) {
                        try {
                            return new SimpleDateFormat("MM-dd-YYYY").parse(o1.releaseDate).compareTo(new SimpleDateFormat("MM-dd-YYYY").parse(o2.releaseDate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
            }else{
                Collections.sort(trackList, new Comparator<Track>() {
                    @Override
                    public int compare(Track o1, Track o2) {
                        return ((Double)(o1.trackPrice - o2.trackPrice)).intValue();
                    }
                });
            }
            int track_size = trackList.size();
            if(track_size > 0){
                for(Track track: trackList){
                    trackListMain.add(track);
                }
                myAdapter.notifyDataSetChanged();
            }else{
                Toast.makeText(MainActivity.this,"No Search Results Found!!!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected ArrayList<Track> doInBackground(String... strings) {

            HttpURLConnection connection = null;
            ArrayList<Track> trackList = new ArrayList<>();
            String keyword = strings[0];
            String limit = strings[1];

            try {
                String apiURL = baseURL+"?"+"term="+keyword+"&limit="+limit;
                URL url = new URL(apiURL);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONObject root = new JSONObject(json);
                    JSONArray results = root.getJSONArray("results");
                    for (int i=0;i<results.length();i++) {
                        JSONObject trackJson = results.getJSONObject(i);
                        Log.d("JSON",trackJson.toString());
                        Track trackItem = new Track();
                        trackItem.artist = trackJson.has("artistName") ? trackJson.getString("artistName") : null;
                        trackItem.title = trackJson.has("trackName") ? trackJson.getString("trackName"): null;
                        trackItem.album = trackJson.has("collectionName") ? trackJson.getString("collectionName") : null;
                        trackItem.genre = trackJson.has("primaryGenreName") ? trackJson.getString("primaryGenreName") : null;
                        trackItem.trackPrice = trackJson.has("trackPrice") ? trackJson.getDouble("trackPrice") : 0.0;
                        trackItem.albumPrice = trackJson.has("collectionPrice") ? trackJson.getDouble("collectionPrice"): 0.0;
                        trackItem.imageURL = trackJson.has("artworkUrl100") ? trackJson.getString("artworkUrl100"):null;
                        try{
                            Date dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(trackJson.getString("releaseDate"));
                            trackItem.releaseDate = new SimpleDateFormat("MM-dd-YYYY").format(dateFormat);
                        }catch (ParseException e){
                            e.printStackTrace();
                        }
                        trackList.add(trackItem);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                //Handle Exceptions
                e.printStackTrace();
            } catch (IOException e) {
                //Handle Exceptions
                e.printStackTrace();
            }catch (JSONException e) {
                //Handle Exceptions
                e.printStackTrace();
            }finally {
                //Close the connections
            }
            return trackList;
        }
    }
}
