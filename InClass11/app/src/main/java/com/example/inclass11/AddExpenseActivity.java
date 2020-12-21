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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddExpenseActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    EditText et_name,et_amount;
    Spinner sp_category;
    String selectedCategory;
    Button bt_add,bt_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        setTitle("Add Expense");
        et_amount = findViewById(R.id.et_amount);
        et_name = findViewById(R.id.et_name);
        sp_category = findViewById(R.id.sp_category);
        bt_add = findViewById(R.id.bt_add);
        bt_cancel = findViewById(R.id.bt_cancel);
        selectedCategory = null;

        db = FirebaseFirestore.getInstance();

        if(getIntent() !=null) {
            List<String> categories = new ArrayList<String>();
            categories.add("<-- Select a Category -->");
            categories.add("Groceries");
            categories.add("Invoice");
            categories.add("Transportation");
            categories.add("Shopping");
            categories.add("Rent");
            categories.add("Trips");
            categories.add("Utilities");
            categories.add("Other");

            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(AddExpenseActivity.this,R.layout.support_simple_spinner_dropdown_item,categories);
            categoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            sp_category.setAdapter(categoryAdapter);
            sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedCategory = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            bt_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(et_name.getText().toString().isEmpty()){
                        Toast.makeText(AddExpenseActivity.this,"Enter name of the Expense",Toast.LENGTH_SHORT).show();
                        Log.d("AddExpense", "Name not entered");
                    } else if(et_amount.getText().toString().isEmpty()){
                        Toast.makeText(AddExpenseActivity.this,"Enter amount of the Expense",Toast.LENGTH_SHORT).show();
                        Log.d("AddExpense", "Amount not entered");
                    } else if(sp_category.getSelectedItem().toString().equals("<-- Select a Category -->")){
                        Toast.makeText(AddExpenseActivity.this,"Select a category for the Expense",Toast.LENGTH_SHORT).show();
                        Log.d("AddExpense", "Category not Selected");
                    }else{
                        Expense expense = new Expense(et_name.getText().toString(),sp_category.getSelectedItem().toString(),Double.parseDouble(et_amount.getText().toString()));
                        Date expenseDate = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY");
                        String displayDate = sdf.format(expenseDate);
                        expense.setDate(displayDate);
                        Log.d("AddExpense", expense.toString());
                        addData(expense);
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

    public void addData(final Expense expense){
        db.collection(MainActivity.COLLECTION).add(expense.toHashMap()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("AddExpense", "Added Expense");
                Toast.makeText(AddExpenseActivity.this, "Expense Added", Toast.LENGTH_SHORT).show();
                Intent i = new Intent();
                expense.setId(documentReference.getId());
                i.putExtra(MainActivity.EXPENSE_NAME,expense);
                setResult(MainActivity.RESULT_OK,i);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddExpenseActivity.this, "Expense Not Added, Please try again later", Toast.LENGTH_SHORT).show();
                Log.d("AddExpense", "Expense add failed");
            }
        });
    }
}
