package com.example.inclass11;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class Expense implements Serializable {

    private String name;
    private String Category;
    private Double Amount;
    private String id;
    private String date;
    HashMap<String, Object> hashMap;

    public Expense(String name, String category, Double amount) {
        this.name = name;
        Category = category;
        Amount = amount;
    }

    public Expense(){

    }

    @Override
    public String toString() {
        return "Expense{" +
                "name='" + name + '\'' +
                ", Category='" + Category + '\'' +
                ", Amount=" + Amount +
                ", id='" + id + '\'' +
                ", date=" + date +
                ", hashMap=" + hashMap +
                '}';
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public Double getAmount() {
        return Amount;
    }

    public void setAmount(Double amount) {
        Amount = amount;
    }

    public HashMap<String, Object> toHashMap() {
        hashMap =  new HashMap<String, Object>();
        hashMap.put("name",getName());
        hashMap.put("category", getCategory());
        hashMap.put("amount", getAmount());
        hashMap.put("date",getDate());
        return hashMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static Expense toUser(DocumentSnapshot documentSnapshot){
        Expense expense = new Expense();
        expense.setName(documentSnapshot.getString("name"));
        expense.setCategory(documentSnapshot.getString("category"));
        expense.setAmount((Double) documentSnapshot.get("amount"));
        expense.setId(documentSnapshot.getId());
        expense.setDate(documentSnapshot.getString("date"));
        return expense;
    }
}
