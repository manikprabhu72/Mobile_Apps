package com.example.inclass12;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

/*
InClass12
Group 05
Manik Prabhu Cheekoti
Akhil Reddy Yakkaluri
 */

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText et_email, et_pass;
    Button bt_login,bt_signup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Login");
        et_email = findViewById(R.id.et_email);
        et_pass = findViewById(R.id.et_pass);
        bt_login = findViewById(R.id.bt_login);
        bt_signup = findViewById(R.id.bt_register);
        mAuth = FirebaseAuth.getInstance();
        bt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString();
                String pass = et_pass.getText().toString();
                if(!email.isEmpty() && !pass.isEmpty()){
                    mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Log.d("Main","Signin Successful");
                                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                startContactActivity();
                            }else{
                                Log.d("Main","Invalid Credentials");
                                Toast.makeText(MainActivity.this, "Invalid Credentials: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }).addOnFailureListener(MainActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Invalid Credentials: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else if(email.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter email address", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
           startContactActivity();
        }
    }

    public void startContactActivity(){
        Intent i = new Intent(MainActivity.this, ContactsActivity.class);
        startActivity(i);
    }
}
