package com.example.inclass12;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.HashMap;

public class Contact implements Serializable {
    private String email,name,image_url;
    private long phone;
    HashMap<String, Object> hashMap;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public HashMap<String, Object> toHashMap(){
        hashMap =  new HashMap<String, Object>();
        hashMap.put("name",getName());
        hashMap.put("email", getEmail());
        hashMap.put("phone", getPhone());
        hashMap.put("image",getImage_url());
        return hashMap;
    }

    public static Contact toContact(HashMap<String,Object> contactMap){
        Contact contact = new Contact();
        contact.setEmail(String.valueOf(contactMap.get("email")));
        contact.setName(String.valueOf(contactMap.get("name")));
        contact.setImage_url(String.valueOf(contactMap.get("image")));
        contact.setPhone(Long.parseLong(String.valueOf(contactMap.get("phone"))));
        return contact;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", image_url='" + image_url + '\'' +
                ", phone=" + phone +
                ", hashMap=" + hashMap +
                '}';
    }
}
