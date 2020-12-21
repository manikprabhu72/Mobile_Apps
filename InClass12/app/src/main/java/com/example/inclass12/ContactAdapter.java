package com.example.inclass12;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder>{

    Context c;
    ArrayList<Contact> contacts;
    static AdapterActivity mListener;

    public ContactAdapter(Context c, ArrayList<Contact> contacts) {
        this.c = c;
        this.contacts = contacts;
        this.mListener = (ContactsActivity)c;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view,parent,false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder( MyViewHolder holder, int position) {

        holder.tv_name.setText(contacts.get(position).getName());
        holder.tv_email.setText(contacts.get(position).getEmail());
        holder.tv_phone.setText(String.valueOf(contacts.get(position).getPhone()));
        if(!contacts.get(position).getImage_url().isEmpty()){
            Picasso.get().load(contacts.get(position).getImage_url()).into(holder.iv_contact);
        }
        holder.clickedPosition = position;

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        TextView tv_name, tv_phone, tv_email;
        ImageView iv_contact;
        Context context;
        int clickedPosition;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_email = itemView.findViewById(R.id.tv_email);
            tv_phone = itemView.findViewById(R.id.tv_number);
            iv_contact = itemView.findViewById(R.id.iv_contact);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            mListener.longClick(clickedPosition);
            return false;
        }
    }

    public interface AdapterActivity{
        public void longClick(int position);
    }

}
