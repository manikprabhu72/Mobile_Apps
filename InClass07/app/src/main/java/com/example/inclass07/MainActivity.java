package com.example.inclass07;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


/*
InClass 07
Manik Prabhu Cheekoti
Akhil Reddy Yakkaluri
 */

public class MainActivity extends AppCompatActivity {

    ConstraintLayout cl_loading, cl_ready;
    Button bt_start, bt_exit;
    String baseURL = "http://dev.theappsdr.com/apis/trivia_json/index.php";
    ArrayList<Question> questionsList = new ArrayList<Question>();
    static final String QUESTIONS_KEY = "questions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cl_loading = findViewById(R.id.cl_loading);
        cl_ready = findViewById(R.id.cl_ready);
        bt_exit = findViewById(R.id.bt_exit);
        bt_start = findViewById(R.id.bt_start);

        cl_ready.setVisibility(ConstraintLayout.INVISIBLE);
        bt_start.setEnabled(false);

        bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,TriviaActivity.class);
                intent.putParcelableArrayListExtra(MainActivity.QUESTIONS_KEY,questionsList);
                startActivity(intent);
                //finish();
            }
        });
        if(isConnected()){
            new GetJSONData().execute();

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


    class GetJSONData extends AsyncTask<Void, Void, ArrayList<Question>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(ArrayList<Question> questionsList) {
            super.onPostExecute(questionsList);
            cl_loading.setVisibility(ConstraintLayout.INVISIBLE);
            cl_ready.setVisibility(ConstraintLayout.VISIBLE);
            bt_start.setEnabled(true);

        }

        @Override
        protected ArrayList<Question> doInBackground(Void ...voids) {

            HttpURLConnection connection = null;
            try {
                String apiURL = baseURL;
                URL url = new URL(apiURL);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONObject root = new JSONObject(json);
                    JSONArray questions = root.getJSONArray("questions");
                    for (int i=0;i<questions.length();i++) {
                        JSONObject questionJson = questions.getJSONObject(i);
                        Question question = new Question();
                        question.text = questionJson.has("text") ? questionJson.getString("text") : null;
                        question.imageURL = questionJson.has("image") ? questionJson.getString("image") : null;
                        String choices[] = null;
                        if(questionJson.has("choices")){
                            JSONObject choicesObject = questionJson.getJSONObject("choices");
                            question.answer = choicesObject.has("answer") ? Integer.parseInt(choicesObject.getString("answer")) : -1;
                            JSONArray choicesJSON = choicesObject.has("choice") ? choicesObject.getJSONArray("choice"):null;
                            choices = new String[choicesJSON.length()];
                            for(int j=0; j<choicesJSON.length();j++){
                                choices[j] = choicesJSON.getString(j);
                            }
                        }
                        question.choices =  choices;

                        Log.d("Async: ",question.toString());

                        questionsList.add(question);
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
            return questionsList;
        }
    }
}
