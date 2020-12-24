package com.example.inclass07;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StatsActivity extends AppCompatActivity {

    TextView tv_percent;
    ProgressBar pb_percent;
    Button bt_quit,bt_try_again;

    int percent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        tv_percent = findViewById(R.id.tv_percent);
        pb_percent = findViewById(R.id.pb_percent);
        bt_quit = findViewById(R.id.bt_quit);
        bt_try_again = findViewById(R.id.bt_try_again);

        bt_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                setResult(TriviaActivity.FINISH_CODE,intent2);
                //Intent intent = new Intent(StatsActivity.this,MainActivity.class);
                //startActivity(intent);
                finish();
            }
        });

        bt_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(TriviaActivity.SUCCESS_CODE, intent);
                finish();
            }
        });

        if(getIntent() !=null && getIntent().getExtras() != null){
            int answered = getIntent().getExtras().getInt(TriviaActivity.ANSWERED_CORRECTLY);
            int questions_size = getIntent().getExtras().getInt(TriviaActivity.QUESTIONS_SIZE);
            Log.d("yugu", "ghiu "+answered);
            Log.d("yugu", "abcd "+questions_size);
            percent =(answered*100)/questions_size;
            Log.d("yugu", "percent  "+percent);
            tv_percent.setText(percent+"%");
            pb_percent.setProgress(percent);
        }
    }
}
