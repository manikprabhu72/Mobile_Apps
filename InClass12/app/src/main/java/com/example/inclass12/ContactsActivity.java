package com.example.inclass12;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ContactsActivity extends AppCompatActivity implements ContactAdapter.AdapterActivity{

    ImageButton ib_logout;
    Button bt_new_contact;
    RecyclerView rv_contacts;
    FirebaseFirestore db;
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    UserContacts userContacts;
    ArrayList<Contact> contacts;
    ContactAdapter myAdapter;
    final static String COLLECTION = "user_contacts";
    static final int NEW_REQ_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        setTitle("Contacts");
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        ib_logout = findViewById(R.id.ib_logout);
        bt_new_contact = findViewById(R.id.bt_new_contact);
        rv_contacts = findViewById(R.id.rv_contacts);

        userContacts = new UserContacts();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv_contacts.setLayoutManager(layoutManager);
        rv_contacts.setHasFixedSize(true);


        ib_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
            }
        });
        bt_new_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!userContacts.getDocId().isEmpty()){
                    Intent i = new Intent(ContactsActivity.this, NewContactActivity.class);
                    i.putExtra("docId", userContacts.getDocId());
                    startActivityForResult(i,NEW_REQ_CODE);
                }
            }
        });
        if(getIntent() !=null){
            FirebaseUser user  = mAuth.getCurrentUser();
            db.collection(COLLECTION).whereEqualTo("userId",user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots != null && queryDocumentSnapshots.getDocuments() != null && queryDocumentSnapshots.getDocuments().size() !=0){
                        Log.d("NewContact", mAuth.getCurrentUser().getUid());
                        List<DocumentSnapshot> documentSnapshots = new ArrayList<DocumentSnapshot>();
                        for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            documentSnapshots.add(documentSnapshot);
                        }

                        userContacts = UserContacts.toUserContacts(documentSnapshots.get(0));
                        if(userContacts.getContacts() != null){
                            contacts = new ArrayList<Contact>(Arrays.asList(userContacts.getContacts()));
                        }else{
                            contacts = new ArrayList<Contact>();
                        }
                        myAdapter = new ContactAdapter(ContactsActivity.this, contacts);
                        rv_contacts.setAdapter(myAdapter);

                    }

                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            finish();
        }
    }


    @Override
    public void longClick(int position) {
        contacts.remove(position);
        myAdapter.notifyDataSetChanged();
        Contact[] contactArr = new Contact[contacts.size()];
        userContacts.setContacts(contacts.toArray(contactArr));
        db.collection(COLLECTION).document(userContacts.getDocId()).update(userContacts.toHashMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("ContactsActivity", "Contact Deleted");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  final Intent data) {
        Log.d("ContactsActivity", "Before");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_REQ_CODE && resultCode == RESULT_OK) {
            Log.d("ContactsActivity", "After");
            db.collection(COLLECTION).whereEqualTo("userId",mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    userContacts = UserContacts.toUserContacts(documentSnapshot);
                    Contact contact = (Contact) data.getSerializableExtra("Contact");
                    contacts.add(contact);
                    myAdapter.notifyDataSetChanged();
                    Log.d("ContactsActivity", "Get Called");
                }
            });
        }
    }

}
