package com.example.inclass12;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewContactActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef;
    EditText et_name,et_number, et_email;
    ImageButton ib_prof_pic;
    Button bt_submit, bt_cancel;
    String docId;
    UserContacts userContacts;
    ArrayList<Contact> contacts;
    Contact contact;
    static final int REQUEST_IMAGE_CAPTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);
        setTitle("Create New Contact");
        et_name = findViewById(R.id.et_name);
        et_number = findViewById(R.id.et_number);
        et_email = findViewById(R.id.et_email);
        ib_prof_pic = findViewById(R.id.ib_prof_pic);
        bt_submit = findViewById(R.id.bt_submit);
        bt_cancel = findViewById(R.id.bt_cancel);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        ib_prof_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_name.getText().toString();
                String number = et_number.getText().toString();
                String email = et_email.getText().toString();
                // Get the data from an ImageView as bytes
                ib_prof_pic.setDrawingCacheEnabled(true);
                ib_prof_pic.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) ib_prof_pic.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                if(docId != null){
                    if(!name.isEmpty() && !number.isEmpty() && !email.isEmpty()){
                        contact = new Contact();
                        contact.setPhone(Long.parseLong(number));
                        contact.setEmail(email);
                        contact.setName(name);
                        final StorageReference contactref = storageRef.child("images/"+email+".jpg");
                        UploadTask uploadTask = contactref.putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure( Exception exception) {
                                Log.d("NewContact", "Error in upload");
                                Toast.makeText(NewContactActivity.this, "Image upload Failed, try again later", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d("NewContact", "Image Url: "+contactref.getDownloadUrl());


                            }
                        });
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return contactref.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                contact.setImage_url(task.getResult().toString());
                                db.collection(ContactsActivity.COLLECTION).document(docId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Log.d("NewContact", docId);
                                        userContacts = UserContacts.toUserContacts(documentSnapshot);
                                        Log.d("NewContact","User Contacts: " + userContacts.toString());
                                        if(userContacts.getContacts() != null){
                                            contacts = new ArrayList<Contact>(Arrays.asList(userContacts.getContacts()));
                                        }else{
                                            contacts = new ArrayList<Contact>();
                                        }
                                        contacts.add(contact);
                                        Contact[] contactsArr = new Contact[contacts.size()];
                                        userContacts.setContacts(contacts.toArray(contactsArr));
                                        addContact();
                                    }
                                });
                            }
                        });
                    }else if(name.isEmpty()){
                        Toast.makeText(NewContactActivity.this, " Name Cannot be empty", Toast.LENGTH_SHORT).show();
                    }else if(number.isEmpty()){
                        Toast.makeText(NewContactActivity.this, " Number Cannot be empty", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(NewContactActivity.this, " Email Cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        if(getIntent()!=null && getIntent().getExtras()!=null){
            docId = getIntent().getStringExtra("docId");
        }

    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ib_prof_pic.setImageBitmap(imageBitmap);
        }
    }

    public void addContact(){
        db.collection(ContactsActivity.COLLECTION).document(docId).update(userContacts.toHashMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("NewContact", "Contact Added");
                Intent i = new Intent();
                i.putExtra("Contact",contact);
                setResult(RESULT_OK, i);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("NewContact", "Contact Add Failed");
                Toast.makeText(NewContactActivity.this, "Cotact Add Failed, try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
