package com.example.inclass06;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/*
InClass 06
Group 05
ManiK Prabhu Cheekoti
Akhil Reddy Yakkaluri

 */

public class MainActivity extends AppCompatActivity {

    String baseURL = "http://newsapi.org/v2/top-headlines";
    AlertDialog.Builder builder = null;
    TextView tv_category = null;
    Button bt_select = null;
    ImageView iv_image = null;
    ConstraintLayout cl_loading = null;
    ConstraintLayout cl_news = null;
    TextView tv_title = null;
    TextView tv_time = null;
    TextView tv_desc = null;
    ImageButton ib_next = null;
    ImageButton ib_prev = null;
    TextView tv_current = null;
    int current_news = 0;
    int news_size = 0;
    ArrayList<News> newsListMain = new ArrayList<News>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cl_loading = findViewById(R.id.cl_loading);
        cl_news = findViewById(R.id.cl_news);
        tv_category = findViewById(R.id.tv_category);
        bt_select = findViewById(R.id.bt_select);
        tv_category = findViewById(R.id.tv_category);
        tv_title =findViewById(R.id.tv_title);
        tv_time =findViewById(R.id.tv_time);
        tv_desc =findViewById(R.id.tv_desc);
        tv_current = findViewById(R.id.tv_current);
        iv_image = findViewById(R.id.iv_image);

        tv_current.setVisibility(TextView.INVISIBLE);
        ib_next = findViewById(R.id.ib_next);
        ib_prev = findViewById(R.id.ib_previous);
        ib_prev.setEnabled(false);
        ib_next.setEnabled(false);
        cl_news.setVisibility(ConstraintLayout.INVISIBLE);
        cl_loading.setVisibility(ConstraintLayout.INVISIBLE);



        if(isConnected()){

            builder = new AlertDialog.Builder(MainActivity.this);
            final String categories[] = {"business","entertainment", "general", "health", "science", "sports", "technology"};
            builder.setTitle("Choose Category").setItems(categories, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String keyword = categories[which];
                            tv_category.setText(keyword);
                            new GetJSONData().execute(keyword,"us");
                        }
                    });
            bt_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.create().show();
                }
            });

            ib_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(current_news == news_size-1){
                        current_news = 0;
                    }else{
                        current_news++;
                    }
                    News newsItem = newsListMain.get(current_news);
                    if(newsItem.imageURL != null && !newsItem.imageURL.isEmpty()){
                        Picasso.get().load(newsItem.imageURL).into(iv_image);
                    } else{
                        Toast.makeText(MainActivity.this,"No Image URL Found!!!", Toast.LENGTH_SHORT).show();
                        iv_image.setVisibility(ImageView.INVISIBLE);
                    }


                    tv_title.setText(newsItem.title);
                    tv_time.setText(newsItem.publishedAt);
                    tv_desc.setText(newsItem.description);
                    tv_current.setText(current_news+1 + " out of "+news_size);
                }
            });


            ib_prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(current_news == 0){
                        current_news = news_size-1;
                    }else{
                        current_news--;
                    }
                    News newsItem = newsListMain.get(current_news);
                    if(newsItem.imageURL != null && !newsItem.imageURL.isEmpty()){
                        Picasso.get().load(newsItem.imageURL).into(iv_image);
                    } else{
                        Toast.makeText(MainActivity.this,"No Image URL Found!!!", Toast.LENGTH_SHORT).show();
                        iv_image.setVisibility(ImageView.INVISIBLE);
                    }

                    tv_title.setText(newsItem.title);
                    tv_time.setText(newsItem.publishedAt);
                    tv_desc.setText(newsItem.description);
                    tv_current.setText(current_news+1 + " out of "+news_size);
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

    class GetJSONData extends AsyncTask<String, Void, ArrayList<News>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cl_loading.setVisibility(ConstraintLayout.VISIBLE);
            cl_news.setVisibility(ConstraintLayout.INVISIBLE);
            tv_current.setVisibility(TextView.INVISIBLE);
            ib_next.setEnabled(false);
            ib_prev.setEnabled(true);
            current_news = 0;
            news_size = 0;
        }

        @Override
        protected void onPostExecute(ArrayList<News> newsList) {
            super.onPostExecute(newsList);
            cl_loading.setVisibility(ConstraintLayout.INVISIBLE);
            cl_news.setVisibility(ConstraintLayout.VISIBLE);
            tv_current.setVisibility(TextView.VISIBLE);
            news_size = newsList.size();
            if(news_size > 0){
                News firstItem = newsList.get(current_news);
                if(firstItem.imageURL != null && !firstItem.imageURL.isEmpty()){
                    Picasso.get().load(firstItem.imageURL).into(iv_image);
                } else{
                    Toast.makeText(MainActivity.this,"No Image URL Found!!!", Toast.LENGTH_SHORT).show();
                    iv_image.setVisibility(ImageView.INVISIBLE);
                }

                tv_current.setText(current_news+1 + " out of "+news_size);
                tv_title.setText(firstItem.title);
                tv_time.setText(firstItem.publishedAt);
                tv_desc.setText(firstItem.description);
                if(news_size > 1){
                    ib_prev.setEnabled(true);
                    ib_next.setEnabled(true);
                }
            }else{
                cl_news.setVisibility(ConstraintLayout.INVISIBLE);
                tv_current.setVisibility(TextView.INVISIBLE);
                Toast.makeText(MainActivity.this,"No News Item Found!!!", Toast.LENGTH_SHORT).show();
            }
            for(News newsItem: newsList){
                Log.d("News: ", "Article: "+newsItem.toString());
            }
        }

        @Override
        protected ArrayList<News> doInBackground(String... strings) {

            HttpURLConnection connection = null;
            ArrayList<News> newsList = new ArrayList<>();
            String category = strings[0];
            String country = strings[1];

            try {
                String apiURL = baseURL+"?"+"apiKey="+getResources().getString(R.string.news_key)+"&country="+URLEncoder.encode(country,"UTF-8")+"&category="+URLEncoder.encode(category,"UTF-8");
                URL url = new URL(apiURL);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONObject root = new JSONObject(json);
                    JSONArray articles = root.getJSONArray("articles");
                    for (int i=0;i<articles.length();i++) {
                        JSONObject artcleJson = articles.getJSONObject(i);
                        News newsItem = new News();
                        newsItem.title = artcleJson.getString("title");
                        newsItem.imageURL = artcleJson.getString("urlToImage");
                        newsItem.description = artcleJson.getString("description");
                        newsItem.publishedAt = artcleJson.getString("publishedAt");

                        newsList.add(newsItem);
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
            newsListMain = newsList;
            return newsList;
        }
    }
}
