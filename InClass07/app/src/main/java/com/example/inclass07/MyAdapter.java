package com.example.inclass07;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context c;
    String choices[];
    static AdapterActivity mListener;

    public MyAdapter(Context c, String choices[]) {
        this.c = c;
        this.choices = choices;
        this.mListener = (TriviaActivity)c;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view,parent,false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_option.setText(choices[position]);
        holder.clickedPosition = position+1;

    }

    @Override
    public int getItemCount() {
        return choices.length;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tv_option;
        Context context;
        int clickedPosition;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_option = itemView.findViewById(R.id.tv_option);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.clickedOption(clickedPosition);
        }
    }

    public interface AdapterActivity{
        public void clickedOption(int position);
    }

}
