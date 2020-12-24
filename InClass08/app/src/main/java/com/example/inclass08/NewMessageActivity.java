package com.example.inclass08;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NewMessageActivity extends AppCompatActivity {

    Spinner userDropDown;
    EditText et_subject, et_message;
    Button bt_send,bt_cancel;
    private final OkHttpClient client = new OkHttpClient();
    User users[];
    String receiver_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        setTitle("Create New Email");
        userDropDown = findViewById(R.id.sp_users);
        et_subject = findViewById(R.id.et_subject);
        et_message = findViewById(R.id.et_message);
        bt_send = findViewById(R.id.bt_send);
        bt_cancel = findViewById(R.id.bt_cancel);

        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestBody formBody = new FormBody.Builder()
                        .add("receiver_id", receiver_id).add("subject",et_subject.getText().toString())
                        .add("message",et_message.getText().toString())
                        .build();

                String defaultToken = getString(R.string.default_token);
                String token = getApplicationContext().getSharedPreferences(getString(R.string.PREFERENCE),MODE_PRIVATE).getString(getString(R.string.token_key),defaultToken);
                Request request = new Request.Builder()
                        .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/add")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addHeader("Authorization","BEARER "+token)
                        .post(formBody).build();

                client.newCall(request).enqueue(new Callback() {
                    @Override public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override public void onResponse(Call call, Response response) throws IOException {
                        try (ResponseBody responseBody = response.body()) {
                            final String jsonData = responseBody.string();
                            JSONObject jsonObject = new JSONObject(jsonData);
                            if (!response.isSuccessful()){
                                final String error = jsonObject.getString("message");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(NewMessageActivity.this,error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(NewMessageActivity.this,"Message is sent", Toast.LENGTH_SHORT).show();
                                        Log.d("New Message Sent", jsonData);
                                        finish();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(getIntent() !=null){
            String defaultToken = getString(R.string.default_token);
            String token = getApplicationContext().getSharedPreferences(getString(R.string.PREFERENCE),MODE_PRIVATE).getString(getString(R.string.token_key),defaultToken);
            Request request = new Request.Builder()
                    .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/users")
                    .addHeader("Authorization","BEARER "+token)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        String jsonData = responseBody.string();
                        Log.d("New Message",jsonData);
                        JSONObject jsonObject = new JSONObject(jsonData);
                        if (!response.isSuccessful()){
                            final String error = jsonObject.getString("message");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(NewMessageActivity.this,error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            GsonBuilder gsonBuilder = new GsonBuilder();
                            //gsonBuilder.setDateFormat("MMM dd, yyyy");
                            String usrArr = jsonObject.getJSONArray("users").toString();
                            users = gsonBuilder.create().fromJson(usrArr,User [].class);

                            Log.d("New Message","Users Length: "+users.length);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    List<String> usernameList = new ArrayList<String>();
                                    for(int i=0;i<users.length;i++){
                                        usernameList.add(users[i].fname + " "+users[i].lname);
                                    }
                                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(NewMessageActivity.this, android.R.layout.simple_spinner_item,usernameList);
                                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    userDropDown.setAdapter(dataAdapter);
                                }
                            });

                            userDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    receiver_id = users[position].id;
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }

                        Log.d("Inbox",jsonData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }
}
