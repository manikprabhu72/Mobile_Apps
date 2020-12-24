package com.example.inclass08;

/*
InClass 08
Group 05
Manik Prabhu Cheekoti
Akhil Reddy Yakkaluru
 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http2.Header;

public class MainActivity extends AppCompatActivity {
    EditText et_email, et_pass;
    Button bt_login,bt_signup;
    private final OkHttpClient client = new OkHttpClient();
    static final String USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Mailer");
        et_email = findViewById(R.id.et_email);
        et_pass = findViewById(R.id.et_pass);
        bt_login = findViewById(R.id.bt_login);
        bt_signup = findViewById(R.id.bt_signup);

        if(isConnected()){
            bt_signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,SignupActivity.class);
                    startActivity(intent);
                }
            });

            bt_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RequestBody formBody = new FormBody.Builder()
                            .add("email", et_email.getText().toString()).add("password",et_pass.getText().toString())
                            .build();

                    Request request = new Request.Builder()
                            .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/login")
                            .post(formBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override public void onResponse(Call call, Response response) throws IOException {
                            try (ResponseBody responseBody = response.body()) {
                                String jsonData = responseBody.string();
                                Log.d("Login",jsonData);
                                JSONObject jsonObject = new JSONObject(jsonData);
                                if (!response.isSuccessful()){
                                    final String error = jsonObject.getString("message");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this,error, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    FieldNamingStrategy customPolicy = new FieldNamingStrategy() {
                                        @Override
                                        public String translateName(Field f) {
                                            Log.d("Main","user_"+f.getName());
                                            return "user_"+f.getName();
                                        }
                                    };

                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.PREFERENCE),MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(getString(R.string.token_key),jsonObject.getString("token"));
                                    editor.commit();
                                    Log.d("Main", jsonObject.getString("token"));
                                    GsonBuilder gsonBuilder = new GsonBuilder();
                                    gsonBuilder.setFieldNamingStrategy(customPolicy);
                                    final User user = gsonBuilder.create().fromJson(jsonData,User.class);
                                    Log.d("Main",user.toString());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(MainActivity.this,InboxActivity.class);
                                            intent.putExtra(MainActivity.USER,user);
                                            startActivity(intent);
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
}
