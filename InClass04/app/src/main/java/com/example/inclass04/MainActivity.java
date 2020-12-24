/*
Homework_01.
Group 05,
Manik Prabhu Cheekoti
Akhil Reddy Yakkaluri
 */


package com.example.inclass04;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "demo";
    private ProgressBar progressBar;
    private SeekBar seekBar;
    private TextView tv_min;
    private TextView tv_times;
    private TextView tv_max;
    private TextView tv_avg;
    private Button bt_calc;
    private Dialog progressDialog;
    private ConstraintLayout cl_progress;
    private ConstraintLayout cl_main;
    private Handler handler;
    private ExecutorService threadPool;
    private ArrayList<Double> doubles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        tv_times = findViewById(R.id.tv_times);
        tv_min = findViewById(R.id.tv_min);
        tv_max = findViewById(R.id.tv_max);
        tv_avg = findViewById(R.id.tv_average);
        seekBar = findViewById(R.id.seekBar);
        bt_calc = findViewById(R.id.bt_calc);
        cl_main = findViewById(R.id.cl_main);
        cl_progress = findViewById(R.id.cl_progress);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if(GetNumbers.STATUS_START == msg.what){
                    cl_progress.setVisibility(ConstraintLayout.VISIBLE);
                    cl_progress.bringToFront();
                    cl_main.setBackgroundColor(Color.DKGRAY);
                    cl_progress.setBackgroundColor(Color.WHITE);
                }else if(GetNumbers.STATUS_STOP == msg.what){
                    tv_min.setText("Minimum:  "+findMin(doubles));
                    tv_max.setText("Maximum:  "+findMax(doubles));
                    tv_avg.setText("Average:  "+findAvg(doubles));
                    cl_progress.setVisibility(ConstraintLayout.INVISIBLE);
                    cl_main.setBackgroundColor(Color.WHITE);
                }
                return false;
            }
        });

        threadPool = Executors.newFixedThreadPool(2);

        seekBar.setMin(1);
        cl_progress.setVisibility(ConstraintLayout.INVISIBLE);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_times.setText(progress + " Times");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        bt_calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new GetNumbers().execute(seekBar.getProgress());
                threadPool.execute(new GetNumbers());
            }
        });

    }

    class GetNumbers implements Runnable{
        static final int STATUS_START = 0x00;
        static final int STATUS_STOP = 0x01;
        @Override
        public void run() {
            Message startMessage = new Message();
            Message stopMessage = new Message();
            startMessage.what = STATUS_START;
            handler.sendMessage(startMessage);
            doubles = HeavyWork.getArrayNumbers(seekBar.getProgress());
            stopMessage.what = STATUS_STOP;
            handler.sendMessage(stopMessage);
        }

        /*@Override
        protected void onPreExecute() {
            super.onPreExecute();
            cl_progress.setVisibility(ConstraintLayout.VISIBLE);
            cl_progress.bringToFront();
            cl_main.setBackgroundColor(Color.DKGRAY);
            cl_progress.setBackgroundColor(Color.WHITE);
            Log.d("xyz", progressBar.getVisibility() + " "+cl_progress.getVisibility());

        }

        @Override
        protected ArrayList<Double> doInBackground(Integer... integers) {
            return HeavyWork.getArrayNumbers(integers[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Double> doubles) {
            super.onPostExecute(doubles);
            Log.d(TAG,"onPostExecute: "+doubles.toString());
            double min  = findMin(doubles);
            tv_min.setText("Minimum:  "+min);
            tv_max.setText("Maximum:  "+findMax(doubles));
            tv_avg.setText("Average:  "+findAvg(doubles));
            cl_progress.setVisibility(ConstraintLayout.INVISIBLE);
            cl_main.setBackgroundColor(Color.WHITE);
        }*/
    }

    private double findMin(ArrayList<Double> doubles){
        double min = Double.MAX_VALUE;

        for(double d:doubles){
            if(d<min){
                min =d;
            }
        }

        return min;
    }

    private double findMax(ArrayList<Double> doubles){
        double max = Double.MIN_VALUE;

        for(double d:doubles){
            if(d>max){
                max =d;
            }
        }

        return max;
    }

    private double findAvg(ArrayList<Double> doubles){
        double sum = 0;

        for(double d:doubles){
            sum = sum + d;
        }

        return sum/doubles.size();
    }
}
