package com.example.inclass14;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.MyViewHolder>{

    Context c;
    ArrayList<Trip> trips;
    static TripAdapterActivity mListener;

    public TripAdapter(Context c, ArrayList<Trip> trips) {
        this.c = c;
        this.trips = trips;
        this.mListener = (MainActivity)c;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view_main,parent,false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tv_trip.setText(trips.get(position).getName());
        holder.tv_place.setText(trips.get(position).getDescription());
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);
        holder.rv_places.setLayoutManager(layoutManager);
        holder.rv_places.setHasFixedSize(true);
        holder.placeAdapter = new PlaceAdapter(c,trips.get(position).getResults(),position);
        holder.rv_places.setAdapter(holder.placeAdapter);
        holder.clickedPosition = position;

    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tv_trip,tv_place;
        ImageButton ib_location, ib_add_place;
        RecyclerView rv_places;
        PlaceAdapter placeAdapter;

        int clickedPosition;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_trip = itemView.findViewById(R.id.tv_trip);
            tv_place = itemView.findViewById(R.id.tv_place);
            ib_location = itemView.findViewById(R.id.ib_location);
            ib_add_place = itemView.findViewById(R.id.ib_add_place);
            rv_places = itemView.findViewById(R.id.rv_places);

            ib_add_place.setOnClickListener(this);
            ib_location.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == ib_add_place.getId()){
                mListener.onClickAddPlace(clickedPosition);
            }else{
                mListener.onClickLocation(clickedPosition);
            }

        }
    }

    public interface TripAdapterActivity{
        public void onClickAddPlace(int position);
        public void onClickLocation(int position);
    }

}
