package com.example.inclass07;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TriviaActivity extends AppCompatActivity implements MyAdapter.AdapterActivity {

    ConstraintLayout cl_loading;
    RecyclerView rv_choices;
    TextView tv_question,tv_timer,tv_question_text;
    ImageView iv_image;

    Button bt_quit,bt_next;
    MyAdapter myAdapter;

    static final int SUCCESS_CODE = 2;
    static final int REQUEST_CODE = 1;
    static final int FINISH_CODE= 3;
    int current_position = 0;
    int questionsSize = 0;
    int correctlyAnswered = 0;
    static final String ANSWERED_CORRECTLY = "Answered Correctly";
    static final String QUESTIONS_SIZE = "No of Questions";
    ArrayList<Question> questionsList;
    Question currentQuestion = new Question();
    MyCountDownTimer countDownTimer = new MyCountDownTimer(120000,1000);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("vdv","Before");
        if(requestCode == REQUEST_CODE){
            if(resultCode == SUCCESS_CODE){
                Log.d("vdv","In Result");
                current_position = 0;
                correctlyAnswered = 0;
                countDownTimer.start();
                displayQuestion(current_position);
            }
            Log.d("vdv","Out Finish");
            if(resultCode == FINISH_CODE){
                Log.d("vdv","In Finish");
                Log.d("vdv","AFter");
                //Intent intent = new Intent(TriviaActivity.this,MainActivity.class);
                //startActivity(intent);
                finish();
            }
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);

        cl_loading = findViewById(R.id.cl_loading);
        rv_choices = findViewById(R.id.rv_choices);
        tv_question = findViewById(R.id.tv_question);
        tv_question_text = findViewById(R.id.tv_question_text);
        iv_image = findViewById(R.id.iv_image);
        tv_timer = findViewById(R.id.tv_timer);
        bt_quit = findViewById(R.id.bt_quit);
        bt_next = findViewById(R.id.bt_next);
        bt_next.setEnabled(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv_choices.setLayoutManager(layoutManager);
        rv_choices.setHasFixedSize(true);

        if(getIntent() !=null && getIntent().getExtras() != null){
            questionsList = getIntent().getExtras().getParcelableArrayList(MainActivity.QUESTIONS_KEY);
            questionsSize = questionsList.size();
            Log.d("Intent","Size: "+questionsSize);
            bt_next.setEnabled(true);
            displayQuestion(current_position);
            countDownTimer.start();
        }

        bt_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(TriviaActivity.this,MainActivity.class);
                //startActivity(intent);
                finish();
            }
        });

        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Next: ", "Size: "+questionsSize);
                Log.d("Next: ", "Current Position: "+current_position);

                if(current_position < questionsSize-1){
                    current_position++;
                    displayQuestion(current_position);
                } else {
                    finishTriviaActivity();
                }
            }
        });
    }

    public void displayQuestion(int position){
        currentQuestion = questionsList.get(position);
        tv_question.setText("Q"+(position+1));
        tv_question_text.setText(currentQuestion.text);
        if(currentQuestion.imageURL != null && !currentQuestion.imageURL.isEmpty()){
            iv_image.setVisibility(ConstraintLayout.INVISIBLE);
            Picasso.get().load(currentQuestion.imageURL).into(iv_image);
            iv_image.setVisibility(ConstraintLayout.VISIBLE);
        } else{
            iv_image.setImageResource(R.mipmap.noimagefound);
            Toast.makeText(TriviaActivity.this,"No Image URL Found!!!", Toast.LENGTH_SHORT).show();
        }
        myAdapter = new MyAdapter(TriviaActivity.this,currentQuestion.choices);
        rv_choices.setAdapter(myAdapter);
    }

    @Override
    public void clickedOption(int position) {
        Log.d("clicked","answered "+correctlyAnswered);

        if(position == currentQuestion.answer){
            correctlyAnswered++;
        }
        if(current_position < questionsSize-1){
            current_position++;
            displayQuestion(current_position);
        } else {
            finishTriviaActivity();
        }

    }

    public void finishTriviaActivity(){
        Intent intent = new Intent(TriviaActivity.this, StatsActivity.class);
        Log.d("yugu", "ghiu "+correctlyAnswered);
        intent.putExtra(TriviaActivity.ANSWERED_CORRECTLY, correctlyAnswered);
        intent.putExtra(TriviaActivity.QUESTIONS_SIZE, questionsList.size());
        //intent.putParcelableArrayListExtra(MainActivity.QUESTIONS_KEY,questionsList);
        startActivityForResult(intent,REQUEST_CODE);

        //finish();
    }


    public class MyCountDownTimer extends CountDownTimer{

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            tv_timer.setText("Time Left: "+ (millisUntilFinished/1000)+" seconds");

        }

        @Override
        public void onFinish() {
            finishTriviaActivity();
        }
    }
}
