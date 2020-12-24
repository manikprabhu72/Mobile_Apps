package com.example.inclass08;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class DisplayMessageActivity extends AppCompatActivity {

    TextView tv_subject,tv_date,tv_message,tv_sender;
    Button bt_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        setTitle("Email Details");
        tv_date = findViewById(R.id.tv_date);
        tv_subject = findViewById(R.id.tv_subject);
        tv_message = findViewById(R.id.tv_message);
        tv_sender = findViewById(R.id.tv_sender);
        bt_close = findViewById(R.id.bt_close);
        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(getIntent() !=null && getIntent().getExtras() != null){
            Message message = (Message)getIntent().getSerializableExtra(InboxActivity.MESSAGE_KEY);
            tv_subject.setText(message.subject);
            tv_message.setText(message.message);
            tv_sender.setText(message.sender_fname+" "+message.sender_lname);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            tv_date.setText(dateFormat.format(message.created_at));
        }
    }
}
