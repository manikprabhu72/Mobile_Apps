package com.example.inclass05;

/*
InClass 05
Group 05
Manik Prabhu Cheekoti
Akhil Reddy Yakkaluru
 */

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    AlertDialog.Builder builder = null;
    TextView tv_key = null;
    Button bt_go = null;
    ImageView iv_image = null;
    ProgressBar pb_image = null;
    ImageButton ib_next = null;
    ImageButton ib_prev = null;
    int img_num = 0;
    int current_img = 0;
    ArrayList<String> imgURLs = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        builder = new AlertDialog.Builder(MainActivity.this);
        tv_key = findViewById(R.id.tv_key);
        bt_go = findViewById(R.id.bt_go);
        iv_image = findViewById(R.id.iv_image);
        pb_image = findViewById(R.id.pb_image);
        ib_next = findViewById(R.id.ib_next);
        ib_prev = findViewById(R.id.ib_prev);

        pb_image.setVisibility(ProgressBar.INVISIBLE);
        ib_prev.setEnabled(false);
        ib_next.setEnabled(false);
        if(isConnected()){
            new GetKeywords().execute();
            bt_go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.create().show();
                }
            });
            ib_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(current_img == img_num-1){
                        current_img = 0;
                    }else{
                        current_img++;
                    }
                    new GetImage(imgURLs.get(current_img)).execute(imgURLs.get(current_img));
                }
            });


            ib_prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(current_img == 0){
                        current_img = img_num-1;
                    }else{
                        current_img--;
                    }
                    new GetImage(imgURLs.get(current_img)).execute(imgURLs.get(current_img));
                }
            });
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

    class GetKeywords extends AsyncTask<Void , Void , String[]>{
        StringBuilder stringBuilder = new StringBuilder();
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String[] keywords = null;

        @Override
        protected String[] doInBackground(Void... voids) {
            try {
                URL url = new URL("http://dev.theappsdr.com/apis/photos/keywords.php");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    keywords = stringBuilder.toString().split(";");

                    Log.d("Main",keywords.toString());
                }
            } catch (MalformedURLException e) {
                //Handle the exceptions

            } catch (IOException io){

            }
            finally {
                //Close open connections and reader
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return keywords;
        }

        @Override
        protected void onPostExecute(final String[] strings) {
            super.onPostExecute(strings);
            builder.setTitle("Select a Keyword")
                    .setItems(strings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String keyword = strings[which];
                            new GetImageURLs(keyword).execute(keyword);
                        }
                    });
            builder.create().hide();
        }
    }

    class GetImageURLs extends AsyncTask<String, Void, ArrayList<String>>{
        HttpURLConnection connection = null;
        String keyword = null;
        public GetImageURLs(String keyword){
            this.keyword = keyword;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tv_key.setText(keyword);
            current_img = 0;
            img_num = 0;
            imgURLs = null;
            ib_prev.setEnabled(false);
            ib_next.setEnabled(false);
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            img_num = strings.size();
            Log.d("ImageUrls: ", "length: " +img_num + strings.size());
            if(img_num > 0){
                imgURLs = strings;
                new GetImage(strings.get(current_img)).execute(strings.get(current_img));
                if(img_num > 1){
                    ib_next.setEnabled(true);
                    ib_prev.setEnabled(true);
                }
            }

        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            String result = null;
            ArrayList<String> results = new ArrayList<String>();
            try {
                String strUrl = "http://dev.theappsdr.com/apis/photos/index.php" + "?" +
                        "keyword=" + URLEncoder.encode(strings[0], "UTF-8");
                URL url = new URL(strUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    result = IOUtils.toString(connection.getInputStream(), "UTF8");
                    String[] urls = result.split("\n");
                    for(String imageUrl: urls){
                        results.add(imageUrl.trim());
                    }
                }
                Log.d("GetImageURLs: ", results.toString());
            } catch (UnsupportedEncodingException e) {
                //Handle the exceptions
            }catch (MalformedURLException e){

            }catch (IOException e){

            } finally {
                //Close open connections and reader
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return results;
        }
    }

    class GetImage extends AsyncTask<String, Void, Void >{

        Bitmap bitmap = null;
        String url = null;

        public GetImage(String url){
            this.url = url;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            iv_image.setImageResource(R.drawable.image_border);
            pb_image.setVisibility(ProgressBar.VISIBLE);
            if(url.isEmpty()){
                Toast toast = Toast.makeText(MainActivity.this,"No Images Found !!!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                pb_image.setVisibility(ProgressBar.INVISIBLE);
                this.cancel(true);
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            bitmap = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                }
            } catch (MalformedURLException e) {
                //Handle the exceptions
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            } finally {
                //Close open connection
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pb_image.setVisibility(ProgressBar.INVISIBLE);
            if (bitmap != null) {
                iv_image.setImageBitmap(bitmap);

            }else{
                Toast toast = Toast.makeText(MainActivity.this, "Image not Found!!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        }
    }
}
