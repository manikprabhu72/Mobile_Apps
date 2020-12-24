package com.example.inclass08;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    String sender_fname, sender_lname, id, sender_id, receiver_id, message, subject;

    Date created_at, updated_at;

    public String getSender_fname() {
        return sender_fname;
    }

    public void setSender_fname(String sender_fname) {
        this.sender_fname = sender_fname;
    }

    public String getSender_lname() {
        return sender_lname;
    }

    public void setSender_lname(String sender_lname) {
        this.sender_lname = sender_lname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
}
