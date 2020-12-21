package com.example.inclass11;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DisplayExpenseActivity extends AppCompatActivity {

    TextView tv_name_val,tv_category_val,tv_amount_val,tv_date_val;
    Button bt_edit, bt_close;
    Expense expense;
    static final int REQ_CODE_EDIT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_expense);
        setTitle("Show Expense");
        tv_amount_val = findViewById(R.id.tv_amount_val);
        tv_category_val = findViewById(R.id.tv_category_val);
        tv_name_val = findViewById(R.id.tv_name_val);
        tv_date_val = findViewById(R.id.tv_date_val);
        bt_close = findViewById(R.id.bt_close);
        bt_edit = findViewById(R.id.bt_edit);

        bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expense!=null){
                    Intent i = new Intent(DisplayExpenseActivity.this,EditExpenseActivity.class);
                    i.putExtra(MainActivity.EXPENSE_NAME,expense);
                    startActivityForResult(i,REQ_CODE_EDIT);
                }
            }
        });
        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(getIntent() !=null && getIntent().getExtras() != null) {
            expense = (Expense) getIntent().getExtras().getSerializable(MainActivity.EXPENSE_NAME);
            tv_name_val.setText(expense.getName());
            tv_category_val.setText(expense.getCategory());
            tv_amount_val.setText("$"+" "+expense.getAmount());
            tv_date_val.setText(expense.getDate());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_CODE_EDIT && resultCode == MainActivity.RESULT_OK){
            Intent i = new Intent();
            setResult(MainActivity.RESULT_OK,i);
            finish();
        }
    }
}
