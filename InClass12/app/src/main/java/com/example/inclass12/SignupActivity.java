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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
    EditText et_fname, et_lname, et_email, et_pass, et_conf_pass;
    Button bt_signup,bt_cancel;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setTitle("Sign Up");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        et_fname = findViewById(R.id.et_fname);
        et_lname = findViewById(R.id.et_lname);
        et_email = findViewById(R.id.et_email);
        et_pass = findViewById(R.id.et_pass);
        et_conf_pass = findViewById(R.id.et_conf_pass);
        bt_signup = findViewById(R.id.bt_register);
        bt_cancel = findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname = et_fname.getText().toString();
                String lname = et_lname.getText().toString();
                String email = et_email.getText().toString();
                String pass = et_pass.getText().toString();
                String conf_pass = et_conf_pass.getText().toString();
                if(!fname.isEmpty() && !lname.isEmpty() &&!email.isEmpty() &&!pass.isEmpty() &&!conf_pass.isEmpty()){
                    if(pass.equals(conf_pass)){
                        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Log.d("Signup", "Signup successful");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                                    hashMap.put("userId", user.getUid());
                                    db.collection(ContactsActivity.COLLECTION).add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d("Signup", "Success: "+documentReference.getId());
                                            startContactActivity();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Signup", "Signup error");
                                            Log.d("Signup", e.getMessage());
                                            Toast.makeText(SignupActivity.this, "Signup error, try again later", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {

                                    Log.d("Signup", "Signup error"+task.getException());
                                    Toast.makeText(SignupActivity.this, "Signup error, try again later "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(SignupActivity.this, "Password and Confirm Password must match", Toast.LENGTH_SHORT).show();
                    }
                }else if(fname.isEmpty()){
                    Toast.makeText(SignupActivity.this, "firtname cannot be empty", Toast.LENGTH_SHORT).show();
                }else if(lname.isEmpty()){
                    Toast.makeText(SignupActivity.this, "lastname cannot be empty", Toast.LENGTH_SHORT).show();
                }else if(email.isEmpty()){
                    Toast.makeText(SignupActivity.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                }else if(pass.isEmpty()){
                    Toast.makeText(SignupActivity.this, "password cannot be empty", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SignupActivity.this, "confirm password cannot be empty", Toast.LENGTH_SHORT).show();
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
        Intent i = new Intent(SignupActivity.this, ContactsActivity.class);
        startActivity(i);
        finish();
    }
}
