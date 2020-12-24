/*
Assignment: InClass02
FileName: MainActivity
FullName: 1. Manik Prabhu Cheekoti
Akhil Reddy Yakkaluri
 */

package com.example.inclass02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText et_weight;
    private EditText et_height_feet;
    private EditText et_height_inches;

    private TextView tv_result;
    private TextView tv_verdict;

    private Button button_calculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final double[] weight = {0.0};
        final int[] feet = {0};
        final int[] inches = {0};

        et_weight = findViewById(R.id.et_weight);
        et_height_feet = findViewById(R.id.et_height_feet);
        et_height_inches = findViewById(R.id.et_height_inches);

        tv_result = findViewById(R.id.tv_result);
        tv_verdict = findViewById(R.id.tv_verdict);

        button_calculate = findViewById(R.id.button_calculate);

        button_calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weight_text = et_weight.getText().toString();
                String feet_text = et_height_feet.getText().toString();
                String inches_text = et_height_inches.getText().toString();

                if(weight_text.equals("")){
                    et_weight.setError("Can't be Empty!!!");
                    Toast.makeText(MainActivity.this, "Can't be Empty!!", Toast.LENGTH_SHORT).show();
                }else{
                    weight[0] = Double.parseDouble(weight_text);
                }
                if(feet_text.equals("")){
                    et_height_feet.setError("Can't be Empty!!!");
                }else{
                    feet[0] = Integer.parseInt(feet_text);
                }
                if(inches_text.equals("")){
                    et_height_inches.setError("Can't be Empty!!!");
                }else{
                    inches[0] = Integer.parseInt(inches_text);
                }

                inches[0] = inches[0] + feet[0]*12;

                double bmi = weight[0]/inches[0]/inches[0]*703;

                tv_result.setText("Your BMI: "+bmi);

                if(bmi<18.5){
                    tv_verdict.setText("You are Underweight");
                }else if(bmi>18.5 && bmi<=24.9){
                    tv_verdict.setText("Your weight is normal");
                }else if (bmi > 24.9 && bmi <=29.9){
                    tv_verdict.setText("You are Overweight");
                }else{
                    tv_verdict.setText("You are Obese");
                }
            }
        });
    }
}
