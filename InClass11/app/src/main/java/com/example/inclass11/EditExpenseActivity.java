package com.example.inclass11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditExpenseActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    EditText et_name,et_amount;
    Spinner sp_category;
    String selectedCategory;
    Button bt_save,bt_cancel;
    Expense expense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);
        setTitle("Edit Expense");
        et_amount = findViewById(R.id.et_amount);
        et_name = findViewById(R.id.et_name);
        sp_category = findViewById(R.id.sp_category);
        bt_save = findViewById(R.id.bt_save);
        bt_cancel = findViewById(R.id.bt_cancel);
        selectedCategory = null;

        db = FirebaseFirestore.getInstance();

        if(getIntent() !=null && getIntent().getExtras() != null) {
            expense = (Expense) getIntent().getExtras().getSerializable(MainActivity.EXPENSE_NAME);
            List<String> categories = new ArrayList<String>();
            categories.add("Groceries");
            categories.add("Invoice");
            categories.add("Transportation");
            categories.add("Shopping");
            categories.add("Rent");
            categories.add("Trips");
            categories.add("Utilities");
            categories.add("Other");

            et_name.setText(expense.getName());
            et_amount.setText(expense.getAmount().toString());
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(EditExpenseActivity.this,R.layout.support_simple_spinner_dropdown_item,categories);
            categoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            sp_category.setAdapter(categoryAdapter);
            sp_category.setSelection(categories.indexOf(expense.getCategory()));
            sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedCategory = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            bt_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(et_name.getText().toString().isEmpty()){
                        Toast.makeText(EditExpenseActivity.this,"Enter name of the Expense",Toast.LENGTH_SHORT).show();
                        Log.d("EditExpense", "Name not entered");
                    } else if(et_amount.getText().toString().isEmpty()){
                        Toast.makeText(EditExpenseActivity.this,"Enter amount of the Expense",Toast.LENGTH_SHORT).show();
                        Log.d("EditExpense", "Amount not entered");
                    } else{
                        expense.setName(et_name.getText().toString());
                        expense.setCategory(sp_category.getSelectedItem().toString());
                        expense.setAmount(Double.parseDouble(et_amount.getText().toString()));
                        Date expenseDate = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY");
                        String displayDate = sdf.format(expenseDate);
                        expense.setDate(displayDate);
                        Log.d("EditExpense", expense.toString());
                        db.collection(MainActivity.COLLECTION).document(expense.getId()).update(expense.toHashMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("EditExpense", "Expense Edited");
                                Toast.makeText(EditExpenseActivity.this, "Expense Edited", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent();
                                setResult(MainActivity.RESULT_OK,i);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditExpenseActivity.this, "Expense Not Edited, Please try again later", Toast.LENGTH_SHORT).show();
                                Log.d("EditExpense", "Expense edit failed");
                            }
                        });
                    }
                }
            });

            bt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        }

    }
}
