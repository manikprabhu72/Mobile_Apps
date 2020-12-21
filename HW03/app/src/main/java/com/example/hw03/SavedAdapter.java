package com.example.hw03;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.MyViewHolder> {

    Context c;
    ArrayList<City> cities;
    //static AdapterActivity mListener;

    public SavedAdapter(Context c, ArrayList<City> cities) {
        this.c = c;
        this.cities = cities;
        //this.mListener = (MainActivity)c;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_view,parent,false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.tv_city_rv.setText(cities.get(position).getCityName()+", "+cities.get(position).getCountryName());
        holder.tv_temp_rv.setText("Temperature: "+cities.get(position).getMetricTemp()+" C");
        Date currentDate = new Date();
        long minutes = (currentDate.getTime() - cities.get(position).getLocalObservationDateTime().getTime())/(60 * 1000);
        holder.tv_time_rv.setText("Updated: "+minutes+" minutes ago");
        if(cities.get(position).isSaved()){
            holder.iv_star.setImageResource(android.R.drawable.star_big_on);
        }else{
            holder.iv_star.setImageResource(android.R.drawable.star_big_off);
        }
        holder.clickedPosition = position;
        holder.iv_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cities.get(position).setSaved(!cities.get(position).isSaved());
                if(cities.get(position).isSaved())
                    ((ImageButton) v).setImageResource(android.R.drawable.star_big_on);
                else{
                    ((ImageButton) v).setImageResource(android.R.drawable.star_big_off);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                cities.remove(position);
                notifyDataSetChanged();
                if(cities.size() == 0){
                    ((MainActivity) c).cl_no_save.setVisibility(ConstraintLayout.VISIBLE);
                    ((MainActivity) c).cl_recycler_view.setVisibility(ConstraintLayout.INVISIBLE);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_city_rv, tv_temp_rv, tv_time_rv;
        ImageButton iv_star;
        int clickedPosition = 0;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_city_rv = itemView.findViewById(R.id.tv_city_rv);
            tv_temp_rv = itemView.findViewById(R.id.tv_temp_rv);
            tv_time_rv = itemView.findViewById(R.id.tv_time_rv);
            iv_star = itemView.findViewById(R.id.iv_star);

        }

    }

}