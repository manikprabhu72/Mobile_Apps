package com.example.inclass11;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/*
InClass11
Group05
Manik Prabhu Cheekoti
Akhil Reddy Yakkaluri
 */

public class MainActivity extends AppCompatActivity implements ExpenseAdapter.AdapterActivity{

    private FirebaseFirestore db;
    RecyclerView rv_expenses;
    TextView tv_empty;
    ImageButton iv_add;
    static final String COLLECTION = "expenses";
    static final String EXPENSE_NAME = "expense";
    static final int REQ_CODE= 01;
    static final int RESULT_OK = 02;
    static final int REQ_CODE_DISPLAY= 03;
    ArrayList<Expense> expenses;
    ExpenseAdapter expenseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Expense App");
        rv_expenses = findViewById(R.id.rv_expenses);
        tv_empty = findViewById(R.id.tv_empty);
        iv_add = findViewById(R.id.iv_add);
        rv_expenses.setVisibility(RecyclerView.INVISIBLE);
        tv_empty.setVisibility(TextView.VISIBLE);

        expenses = new ArrayList<Expense>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv_expenses.setLayoutManager(layoutManager);
        rv_expenses.setHasFixedSize(true);

        db = FirebaseFirestore.getInstance();

        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AddExpenseActivity.class);
                startActivityForResult(i,REQ_CODE);
            }
        });

        getData();

    }

    @Override
    public void click(int position) {
        Intent i = new Intent(MainActivity.this, DisplayExpenseActivity.class);
        i.putExtra(EXPENSE_NAME, expenses.get(position));
        startActivityForResult(i,REQ_CODE_DISPLAY);
    }

    @Override
    public void longClick(final int position) {
        final Expense expense = expenses.get(position);
        db.collection(COLLECTION).document(expense.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                expenses.remove(position);
                if(expenses.size() == 0){
                    tv_empty.setVisibility(TextView.VISIBLE);
                    rv_expenses.setVisibility(RecyclerView.INVISIBLE);
                }
                expenseAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Expense Deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Main", "Delete Failed");
                Toast.makeText(MainActivity.this, "Delete Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getData(){
        db.collection(COLLECTION).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                expenses = new ArrayList<Expense>();
                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    Expense expense = Expense.toUser(documentSnapshot);
                    expenses.add(expense);
                }
                if(!expenses.isEmpty()){
                    expenseAdapter = new ExpenseAdapter(MainActivity.this, expenses);
                    rv_expenses.setAdapter(expenseAdapter);
                    tv_empty.setVisibility(TextView.INVISIBLE);
                    rv_expenses.setVisibility(TextView.VISIBLE);
                }else{
                    tv_empty.setVisibility(TextView.VISIBLE);
                    rv_expenses.setVisibility(TextView.INVISIBLE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_CODE && resultCode == RESULT_OK){
            Expense expense = (Expense) data.getSerializableExtra(EXPENSE_NAME);
            getData();
            //expenseAdapter.notifyDataSetChanged();
        }else if(requestCode == REQ_CODE_DISPLAY && resultCode == RESULT_OK){
            getData();
        }
    }
}
