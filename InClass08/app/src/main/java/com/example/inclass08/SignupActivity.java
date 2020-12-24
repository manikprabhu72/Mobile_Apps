package com.example.inclass08;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SignupActivity extends AppCompatActivity {

    EditText et_fname,et_lname,et_pass,et_conf_pass,et_email;
    Button bt_signup,bt_cancel;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        et_fname = findViewById(R.id.et_fname);
        et_lname = findViewById(R.id.et_lname);
        et_email = findViewById(R.id.et_email);
        et_pass = findViewById(R.id.et_password);
        et_conf_pass = findViewById(R.id.et_password_confirm);

        bt_signup = findViewById(R.id.bt_signup);
        bt_cancel = findViewById(R.id.bt_cancel);

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_pass.getText().toString().equals(et_conf_pass.getText().toString())){
                    RequestBody formBody = new FormBody.Builder()
                            .add("email", et_email.getText().toString()).add("password",et_pass.getText().toString())
                            .add("fname",et_fname.getText().toString()).add("lname",et_lname.getText().toString())
                            .build();

                    String defaultToken = getString(R.string.default_token);
                    String token = getApplicationContext().getSharedPreferences(getString(R.string.PREFERENCE),MODE_PRIVATE).getString(getString(R.string.token_key),defaultToken);
                    Request request = new Request.Builder()
                            .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/signup")
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
                                            Toast.makeText(SignupActivity.this,error, Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(SignupActivity.this,"User Created", Toast.LENGTH_SHORT).show();
                                            Log.d("User Created", jsonData);
                                            Intent intent = new Intent(SignupActivity.this,InboxActivity.class);
                                            intent.putExtra(MainActivity.USER,user);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else{
                    Toast.makeText(SignupActivity.this, "Both the passwords must be same", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
