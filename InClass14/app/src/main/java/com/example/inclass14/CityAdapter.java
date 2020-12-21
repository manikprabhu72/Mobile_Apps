package com.example.inclass14;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.MyViewHolder>{

    Context c;
    ArrayList<Trip> trips;
    static CityAdapterActivity mListener;

    public CityAdapter(Context c, ArrayList<Trip> trips) {
        this.c = c;
        this.trips = trips;
        this.mListener = (AddTripActivity)c;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view_trip,parent,false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder( MyViewHolder holder, int position) {

        holder.tv_city_name.setText(trips.get(position).getDescription());

        holder.clickedPosition = position;

    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tv_city_name;

        int clickedPosition;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_city_name = itemView.findViewById(R.id.tv_city_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(clickedPosition);
        }
    }

    public interface CityAdapterActivity{
        public void onClick(int position);
    }

}