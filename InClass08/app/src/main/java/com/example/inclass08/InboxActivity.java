package com.example.inclass08;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

            public class InboxActivity extends AppCompatActivity implements MyAdapter.AdapterActivity{

    TextView tv_name;
    ImageButton ib_new_email;
    ImageButton ib_logout;
    RecyclerView rv_messages;
    ArrayList<Message> messages;
    private final OkHttpClient client = new OkHttpClient();
    MyAdapter myAdapter;
    static final String MESSAGE_KEY = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        setTitle("Inbox");
        tv_name = findViewById(R.id.tv_name);
        ib_new_email = findViewById(R.id.ib_new_email);
        ib_logout = findViewById(R.id.ib_logout);
        rv_messages = findViewById(R.id.rv_messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv_messages.setLayoutManager(layoutManager);
        rv_messages.setHasFixedSize(true);
        ib_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.PREFERENCE),MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.token_key),"token");
                editor.commit();
                finish();
            }
        });

        ib_new_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InboxActivity.this,NewMessageActivity.class);
                startActivity(intent);
            }
        });
        if(getIntent() !=null && getIntent().getExtras() != null){
            User user = (User) getIntent().getExtras().getSerializable(MainActivity.USER);
            Log.d("Inbox",user.toString());
            tv_name.setText(user.fname+" "+user.lname);
            String defaultToken = getString(R.string.default_token);
            String token = getApplicationContext().getSharedPreferences(getString(R.string.PREFERENCE),MODE_PRIVATE).getString(getString(R.string.token_key),defaultToken);
            Log.d("Inbox", "token "+token);
            Request request = new Request.Builder()
                    .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox")
                    .addHeader("Authorization","BEARER "+token)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        String jsonData = responseBody.string();
                        Log.d("Inbox",jsonData);
                        JSONObject jsonObject = new JSONObject(jsonData);
                        if (!response.isSuccessful()){
                            final String error = jsonObject.getString("message");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(InboxActivity.this,error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            GsonBuilder gsonBuilder = new GsonBuilder();
                            //gsonBuilder.setDateFormat("MMM dd, yyyy");
                            gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());
                            String msgArr = jsonObject.getJSONArray("messages").toString();
                            messages = new ArrayList<Message>(Arrays.asList(gsonBuilder.create().fromJson(msgArr, Message[].class)));
                            Log.d("Inbox","Messages Length: "+messages.size());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myAdapter = new MyAdapter(InboxActivity.this,messages);
                                    rv_messages.setAdapter(myAdapter);
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

    @Override
    public void onMessageClicked(int position) {
        Intent intent = new Intent(InboxActivity.this,DisplayMessageActivity.class);
        intent.putExtra(MESSAGE_KEY,messages.get(position));
        startActivity(intent);
    }

    @Override
    public void toDelete(final int position) {
        String defaultToken = getString(R.string.default_token);
        String token = getApplicationContext().getSharedPreferences(getString(R.string.PREFERENCE),MODE_PRIVATE).getString(getString(R.string.token_key),defaultToken);
        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/delete/"+messages.get(position).id)
                .addHeader("Authorization","BEARER "+token)
                .build();

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
                                Toast.makeText(InboxActivity.this,error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InboxActivity.this,"Message Deleted", Toast.LENGTH_SHORT).show();
                                messages.remove(position);
                                myAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
