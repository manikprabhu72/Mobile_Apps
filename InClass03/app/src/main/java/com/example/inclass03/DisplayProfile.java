package com.example.inclass03;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayProfile extends AppCompatActivity {
    TextView tv_name;
    TextView tv_gender;
    ImageView iv_avatar;
    Button bt_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_profile);
        setTitle("Display Profile");
        tv_name = findViewById(R.id.tv_name);
        tv_gender = findViewById(R.id.tv_gender);
        iv_avatar = findViewById(R.id.iv_avatar);
        bt_edit = findViewById(R.id.bt_edit);
        if(getIntent() !=null && getIntent().getExtras() != null){
            final User user = (User)getIntent().getExtras().getSerializable(MainActivity.USER_KEY);
            tv_name.setText("Name: "+user.getFirstName()+" "+user.getLastName());
            tv_gender.setText(user.getGender());
            if(user.getGender().equals("Male")){
                iv_avatar.setImageDrawable(getDrawable(R.drawable.male));
            }else{
                iv_avatar.setImageDrawable(getDrawable(R.drawable.female));
            }
            bt_edit.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent toMainActivity = new Intent(DisplayProfile.this,MainActivity.class);
                    toMainActivity.putExtra(MainActivity.USER_KEY,user);
                    startActivity(toMainActivity);
                    finish();
                }
            });
        }
    }
}
