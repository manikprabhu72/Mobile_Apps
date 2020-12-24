package com.example.inclass03;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class SelectAvatar extends AppCompatActivity implements View.OnClickListener{

    public static final String GENDER = "gender";
    private ImageView iv_male;
    private ImageView iv_female;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_avatar);
        setTitle("Select Avatar");
        iv_female = findViewById(R.id.iv_female);
        iv_male = findViewById(R.id.iv_male);

        iv_male.setOnClickListener(this);
        iv_female.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent returnedData = new Intent();
        if(view == iv_female){
            returnedData.putExtra(GENDER,"female");
        }else{

            returnedData.putExtra(GENDER,"male");
        }
        setResult(RESULT_OK,returnedData);
        finish();
    }
}

