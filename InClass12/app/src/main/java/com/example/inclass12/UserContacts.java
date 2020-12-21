package com.example.inclass12;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UserContacts implements Serializable {
    private String userId, docId;
    private Contact[] contacts;
    HashMap<String, Object> hashMap;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Contact[] getContacts() {
        return contacts;
    }

    public void setContacts(Contact[] contacts) {
        this.contacts = contacts;
    }

    public HashMap<String, Object> toHashMap(){
        hashMap = new HashMap<>();
        hashMap.put("userId", getUserId());
        ArrayList<HashMap<String, Object>> contacts = new ArrayList<>();
        for(int i=0; i <getContacts().length; i++){
            contacts.add(getContacts()[i].toHashMap());
        }
        hashMap.put("contacts", contacts);
        return hashMap;
    }

    public static UserContacts toUserContacts(DocumentSnapshot documentSnapshot){
        UserContacts userContacts = new UserContacts();
        userContacts.setUserId(documentSnapshot.getString("userId"));
        userContacts.setDocId(documentSnapshot.getId());
        ArrayList<HashMap<String,Object>> docs= (ArrayList<HashMap<String,Object>>) documentSnapshot.get("contacts");
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        if(docs != null && docs.size() !=0 ){
            for(int i=0; i < docs.size() ; i++){
                contacts.add(Contact.toContact(docs.get(i)));
            }
        }
        Contact [] contactsArr = new Contact[contacts.size()];
        userContacts.setContacts(contacts.toArray(contactsArr));

        return userContacts;
    }

    @Override
    public String toString() {
        return "UserContacts{" +
                "userId='" + userId + '\'' +
                ", docId='" + docId + '\'' +
                ", contacts=" + Arrays.toString(contacts) +
                ", hashMap=" + hashMap +
                '}';
    }
}
