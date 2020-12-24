package com.example.inclass08;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context c;
    ArrayList<Message> messages;
    static AdapterActivity mListener;

    public MyAdapter(Context c, ArrayList<Message> messages) {
        this.c = c;
        this.messages = messages;
        this.mListener = (InboxActivity)c;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_view,parent,false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_subject.setText(messages.get(position).subject);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        holder.tv_date.setText(dateFormat.format(messages.get(position).created_at));
        holder.clickedPosition = position;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tv_subject,tv_date;
        ImageButton ib_delete;
        int clickedPosition = 0;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_subject = itemView.findViewById(R.id.tv_subject);
            tv_date = itemView.findViewById(R.id.tv_date);
            ib_delete = itemView.findViewById(R.id.ib_delete);

            ib_delete.setOnClickListener(this);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == ib_delete.getId()){
                mListener.toDelete(clickedPosition);
            }else{
                mListener.onMessageClicked(clickedPosition);
            }

        }
    }

    public interface AdapterActivity{
        public void onMessageClicked(int position);
        public void toDelete(int position);
    }

}
